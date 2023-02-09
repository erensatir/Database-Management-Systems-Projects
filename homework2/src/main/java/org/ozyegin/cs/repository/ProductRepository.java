package org.ozyegin.cs.repository;

import org.ozyegin.cs.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class ProductRepository extends JdbcDaoSupport {
  final int batchSize = 10;

  final String createPS = "INSERT INTO product(name, brandName, description) VALUES(?,?,?)";
  final String updatePS = "UPDATE product SET name=?, description=?, brandName=? WHERE id=?";
  final String getPS = "SELECT * FROM product WHERE id IN (:ids)";
  final String getBrandNamePS = "SELECT * FROM product WHERE brandName=?";
  final String getSinglePS = "SELECT * FROM product WHERE id=?";
  final String deleteAllPS = "DELETE FROM product";
  final String deletePS = "DELETE FROM product WHERE id=?";
  final String idForProductPS = "SELECT id FROM product";



  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Integer> intRowMapper=(resultSet, i) -> resultSet.getInt(1);

  private final RowMapper<Product> productRowMapper = (resultSet, i) -> {
    Product product=new Product();
    product.setId(resultSet.getInt("id"));
    product.setName(resultSet.getString("name"));
    product.setDescription(resultSet.getString("description"));
    product.setBrandName(resultSet.getString("brandName"));
    return product;
  };

  public Product find(int id) {
    Product product;
    product = Objects.requireNonNull(getJdbcTemplate()).queryForObject(getSinglePS, new Object[] {id}, productRowMapper);
    return product;
  }

  public List<Product> findMultiple(List<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<>();
    } else {
      Map<String, List<Integer>> params = new HashMap<>() {
        {
          this.put("ids", new ArrayList<>(ids));
        }
      };
      var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
      return template.query(getPS, params, productRowMapper);
    }
  }

  public List<Product> findByBrandName(String brandName) {
    return Objects.requireNonNull(getJdbcTemplate()).query(getBrandNamePS,new Object[] {brandName}, productRowMapper);
  }

  public List<Integer> create(List<Product> products) {
    List<Integer> ids =Objects.requireNonNull((getJdbcTemplate())).query(idForProductPS,intRowMapper);

    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(createPS,products,10,
            (ps,product) -> {
              ps.setString(1, product.getName());
              ps.setString(2, product.getBrandName());
              ps.setString(3, product.getDescription());
            });
    List<Integer> tempids =Objects.requireNonNull(getJdbcTemplate()).query(idForProductPS, intRowMapper);
    tempids.removeAll(ids);
    return tempids;
  };

  public void update(List<Product> products) {
    for (int i = 0;i<products.size();i++) {

      if (Objects.requireNonNull(getJdbcTemplate()).update(updatePS,
              products.get(i).getName(), products.get(i).getDescription(), products.get(i).getBrandName(), products.get(i).getId()) != 1) {
        System.out.println("Update Completed!");
      }
      else {
        System.out.println("Update rejected");
      }
    }

  }

  public void delete(List<Integer> ids) {
    for (int i = 0;i<ids.size();i++) {
      if (Objects.requireNonNull(getJdbcTemplate()).update(deletePS,
              ids.get(i)) != 1) {
        System.out.println("Product Delete is failed!");
      }
    }
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
  }
}