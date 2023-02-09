package org.ozyegin.cs.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class TransactionRepository extends JdbcDaoSupport {

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }
  final String createPS = "INSERT INTO transaction(name,product_id,amount,createdDate)" + " VALUES (?,?,?,?) RETURNING id";
  private final RowMapper<Integer> txnIdMapper = (resultSet, i) -> resultSet.getInt(1);
  public Integer order (String company, int product, int amount, Date createdDate) {
    Timestamp ts = new Timestamp(createdDate.getTime());
    List<Integer> transactionId = Objects.requireNonNull(getJdbcTemplate())
            .query(createPS,
                    (ps) -> {
                      ps.setString(1,company);
                      ps.setInt(2,product);
                      ps.setInt(3,amount);
                      ps.setTimestamp(4,ts);
                    }, txnIdMapper);

    Objects.requireNonNull(getJdbcTemplate()).update("INSERT INTO transactionHistory (transactionHistId,company,product,amount,createdDate) VALUES (?,?,?,?,?)",
            transactionId.get(0),company, product, amount, ts);

    return transactionId.get(0);
  }

  public void delete(int transactionId) throws Exception {
    if (Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM transaction WHERE id=?", transactionId) != 1) {
      throw new Exception("Transaction Delete is failed!");
    }
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM transaction");
  }
}