package org.ozyegin.cs.repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionHistoryRepository extends JdbcDaoSupport {
  private final RowMapper<Pair> pairMapper = (resultSet, i) -> new Pair(
      resultSet.getString(1),
      resultSet.getInt(2)
  );

  private final RowMapper<String> stringMapper = (resultSet, i) -> resultSet.getString(1);

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  final String frstQuery = "SELECT DISTINCT ON (company) company, product " +
          "FROM transactionHistory GROUP BY company, product " +
          "ORDER BY 1,sum(amount) DESC";

  final String scndQuery = "(SELECT name FROM company WHERE name NOT IN (SELECT company FROM transactionHistory" +
          " WHERE createdDate BETWEEN ? AND ?))";

  public List<Pair> query1() {
    return Objects.requireNonNull(getJdbcTemplate()).query(frstQuery,pairMapper);
  }

  public List<String> query2(Date start, Date end) {
    Timestamp frstTs = new Timestamp(start.getTime());
    Timestamp scndTs = new Timestamp(end.getTime());
    return Objects.requireNonNull(getJdbcTemplate()).query(scndQuery,new Object[] {frstTs,scndTs}, stringMapper);
  }

  public void deleteAll() {
  Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM transactionHistory");
  }
}
