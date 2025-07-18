package org.annill.contractor.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.annill.contractor.converter.CountryConverter;
import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.entity.Country;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы со странами
 *
 * @author anailina
 */
@Repository
public class CountryRepository {

    private static final String FIND_ALL_QUERY = "SELECT * FROM country WHERE is_active = true ORDER BY name";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM country WHERE is_active = true AND id = :id";

    private static final String COUNT_BY_ID_QUERY = "SELECT count(*) FROM country WHERE is_active = true AND id = :id";

    private static final String UPDATE_QUERY = "UPDATE country SET name = :name WHERE id = :id";

    private static final String INSERT_QUERY = "INSERT INTO country (id, name, is_active) VALUES (:id, :name, true)";

    private static final String LOGICAL_DELETE_QUERY = "UPDATE country SET is_active = false WHERE id = :id";

    private final CountryConverter countryConverter;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Country> countryRowMapper = (rs, rowNum) -> Country.builder().id(rs.getString("id"))
        .name(rs.getString("name")).isActive(rs.getBoolean("is_active")).build();

    public CountryRepository(CountryConverter countryConverter, NamedParameterJdbcTemplate jdbcTemplate) {
        this.countryConverter = countryConverter;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CountryDto> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, countryRowMapper).stream().map(countryConverter::toDto)
            .collect(Collectors.toList());
    }

    public CountryDto findById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID страны не может быть пустым");
        }

        Country country = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, Map.of("id", id), countryRowMapper);

        return Optional.ofNullable(country).map(countryConverter::toDto)
            .orElseThrow(() -> new EntityNotFoundException("Страна с ID " + id + " не найдена"));
    }

    public void saveOrUpdate(CountryDto country) {
        Integer count = jdbcTemplate.queryForObject(COUNT_BY_ID_QUERY, Map.of("id", country.getId()), Integer.class);

        if (count != null && count > 0) {
            jdbcTemplate.update(UPDATE_QUERY,
                Map.of("id", country.getId(), "name", country.getName(), "is_active", true));
        } else {
            jdbcTemplate.update(INSERT_QUERY,
                Map.of("id", country.getId(), "name", country.getName(), "is_active", true));
        }
    }

    public void logicalDelete(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID страны не может быть пустым");
        }

        int quantityUpdateCountry = jdbcTemplate.update(LOGICAL_DELETE_QUERY, Map.of("id", id));

        if (quantityUpdateCountry == 0) {
            throw new EntityNotFoundException("Страна с ID " + id + " не найдена");
        }
    }

}
