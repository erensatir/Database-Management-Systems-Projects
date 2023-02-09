package org.ozyegin.cs.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class TransactionRepository extends JdbcDaoSupport {

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Integer> orderRowMapper=(resultSet, i) -> resultSet.getInt("id");
  final String createPS = "INSERT INTO transaction(name,product_id,amount,createdDate)" + " VALUES (?,?,?,?)";
  final String deleteAllPS = "DELETE FROM transaction";
  final String deletePS = "DELETE FROM transaction WHERE id=?";

  public Integer order (String company, int product, int amount, Date createdDate) {
    Objects.requireNonNull(getJdbcTemplate().update(createPS, company, product, amount, createdDate));
    List<Integer> temp = Objects.requireNonNull(getJdbcTemplate()).query("SELECT id FROM transaction WHERE createdDate=? AND name=? AND product_id=?", new Object[]{createdDate,company,product},orderRowMapper);

    return temp.get(0);
  }

  public void delete(int transactionId) throws Exception {
    if (Objects.requireNonNull(getJdbcTemplate()).update(deletePS, transactionId) != 1) {
      throw new Exception("Transaction Delete is failed!");
    }
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
  }
}
