package org.annill.contractor.repository;

import java.util.List;
import java.util.Map;
import org.annill.contractor.entity.Country;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Репизоторий страны
 */

@Repository
public class CountryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private RowMapper<Country> countryRowMapper = (rs, rowNum) -> {
        Country c = new Country();
        c.setId(rs.getString("id"));
        c.setName(rs.getString("name"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    };

    public CountryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Country> findAll() {
        String sql = "SELECT * FROM country WHERE is_active = true ORDER BY name";
        return jdbcTemplate.query(sql, countryRowMapper);
    }

    public Country findById(String id) {
        String sql = "SELECT * FROM country WHERE is_active = true AND id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), countryRowMapper);
    }

    public void saveOrUpdate(Country country) {
        String sqlCheck = "SELECT count(*) FROM country WHERE  is_active = true AND id = :id";
        Integer count = jdbcTemplate.queryForObject(sqlCheck, Map.of("id", country.getId()), Integer.class);

        if (count != null && count > 0) {
            String sqlUpdate = """
                UPDATE country
                SET name = :name
                WHERE id = :id
                """;
            jdbcTemplate.update(sqlUpdate, Map.of(
                "id", country.getId(),
                "name", country.getName()
            ));
        } else {
            String sqlInsert = """
                INSERT INTO country (id, name, is_active)
                VALUES (:id, :name, true)
                """;
            jdbcTemplate.update(sqlInsert, Map.of(
                "id", country.getId(),
                "name", country.getName()
            ));
        }
    }

    public void logicalDelete(String id) {
        String sql = "UPDATE country SET is_active = false WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

}
