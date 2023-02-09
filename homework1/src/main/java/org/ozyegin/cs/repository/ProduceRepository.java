package org.ozyegin.cs.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

@Repository
public class ProduceRepository extends JdbcDaoSupport {

  final String sqlCreateProduce = "INSERT INTO produce(name,product_id,capacity)"+ "VALUES (?,?,?)";
  final String deleteAllPS = "DELETE FROM produce";
  final String deletePS = "DELETE FROM produce WHERE id=?";

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Integer> produceRowMapper=(resultSet, i) -> resultSet.getInt("id");

  public Integer produce(String company, int product, int capacity) {
    Objects.requireNonNull(getJdbcTemplate().update(sqlCreateProduce, company, product, capacity));
    List<Integer> temp = Objects.requireNonNull(getJdbcTemplate()).query("SELECT id FROM produce WHERE name=? AND product_id=? AND capacity=?",new Object[]{company, product, capacity},produceRowMapper);
    return temp.get(0);
  }

  public void delete(int produceId) throws Exception {
    if (Objects.requireNonNull(getJdbcTemplate()).update(deletePS,
            produceId) != 1) {
      throw new Exception("Produce Delete is failed!");
    }
  }
  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
  }
}
