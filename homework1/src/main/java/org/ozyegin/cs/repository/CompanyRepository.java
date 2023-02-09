package org.ozyegin.cs.repository;

import org.ozyegin.cs.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;


@Repository
public class CompanyRepository extends JdbcDaoSupport {

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }
  final int batchSize = 10;
  final String createPS = "INSERT INTO company" +
          " VALUES (?,?,?,?,?,?)";
  final String findCountPS = "SELECT * FROM company WHERE country=?";

  final String emailPS = "SELECT email FROM emails WHERE name=?";

  final String selCityPS = "SELECT DISTINCT city FROM company WHERE zip=?";

  final String emailCreatePS = "INSERT INTO emails VALUES(?,?)";

  final String findPS = "SELECT * FROM company WHERE name=?";


  private final RowMapper<String> stringRowMapper = (resultSet, i) -> resultSet.getString(1);


  private final RowMapper<Company> CompanyRowMapper = (resultSet, i) -> {
    Company company = new Company();
    company.setName(resultSet.getString("name"));
    company.setZip(resultSet.getInt("zip"));
    company.setCountry(resultSet.getString("country"));
    company.setCity(resultSet.getString("city"));
    company.setStreetInfo(resultSet.getString("streetInfo"));
    company.setPhoneNumber(resultSet.getString("phoneNumber"));
    company.setE_mails(Objects.requireNonNull(getJdbcTemplate()).queryForList(emailPS, new Object[]{resultSet.getString("name")}, String.class));
    return company;
  };


  public Company find(String name) {
    Company company = null;
    company = Objects.requireNonNull(getJdbcTemplate()).queryForObject(findPS,
            BeanPropertyRowMapper.newInstance(Company.class), name);
    company.setE_mails(Objects.requireNonNull(getJdbcTemplate()).queryForList(emailPS,new Object[] {company.getName()},String.class));
    return company;

  }

  public List<Company> findByCountry(String country) {
    List<Company> companies = Objects.requireNonNull(getJdbcTemplate()).query(findCountPS, new Object[]{country}, CompanyRowMapper);
    return companies;
  };

  public String create(Company company) throws Exception {
    try {
      String c = Objects.requireNonNull(getJdbcTemplate()).queryForObject(selCityPS, new Object[]{company.getZip()}, stringRowMapper);
      if (!c.equals(company.getCity())) {
        throw new Exception("Zip is not equal to city ");
      }
    } catch (EmptyResultDataAccessException e) {
      //throw new Exception("Existing Company data found with no City data");
    }

    if (Objects.requireNonNull(getJdbcTemplate()).update(createPS,
            company.getName(), company.getCountry(), company.getZip(), company.getCity(), company.getStreetInfo(),
            company.getPhoneNumber()) != 1) {
      //ALTER TABLE company ALTER COLUMN e_mails TYPE text;
      throw new Exception("Company creation failed!");
    } else {
      Objects.requireNonNull(getJdbcTemplate()).batchUpdate(emailCreatePS, company.getE_mails(),
              batchSize,
              (ps, email) -> {
                ps.setString(1, company.getName());
                ps.setString(2, email);
              });
    }
    return ("Created Company Named = " + company.getName());

  }

  public String delete(String name) {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM emails WHERE name=?", name);
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM company WHERE name=?", name);
    return name;
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM emails");
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM Company");
  }
}