package org.annill.contractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest
class CountryRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("contractor")
        .withUsername("myuser")
        .withPassword("secret");

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private final CountryDto countryDto = CountryDto.builder().id("Al").name("Албения").build();

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.getJdbcTemplate().execute("TRUNCATE TABLE country cascade");
    }

    @Autowired
    private CountryRepository repository;

    @Test
    void saveOrUpdate_shouldInsertNewContractor() {

        repository.saveOrUpdate(countryDto);

        CountryDto saved = repository.findById(countryDto.getId());
        assertNotNull(saved);
        assertEquals(countryDto, saved);
    }

    @Test
    @Rollback
    void saveOrUpdate_shouldUpdateExistingCountry() {

        repository.saveOrUpdate(countryDto);
        CountryDto newContractorDto = CountryDto.builder()
            .id(countryDto.getId())
            .name("Россия")
            .build();

        repository.saveOrUpdate(newContractorDto);

        CountryDto updated = repository.findById(countryDto.getId());
        assertNotNull(updated);
        assertNotEquals(countryDto.getName(), updated.getName());
    }

    @Test
    void findById_shouldReturnCountry() {
        assertThrows(EmptyResultDataAccessException.class,
            () -> repository.findById(countryDto.getId()));
        repository.saveOrUpdate(countryDto);
        CountryDto newFound = repository.findById(countryDto.getId());
        assertNotNull(newFound);
    }

    @Test
    @Rollback
    void logicalDelete_shouldDeactivateCountry() {

        repository.saveOrUpdate(countryDto);
        assertNotNull(repository.findById(countryDto.getId()));
        repository.logicalDelete(countryDto.getId());

        assertThrows(EmptyResultDataAccessException.class,
            () -> repository.findById(countryDto.getId()));
    }

    @Test
    void findAll_shouldReturnAllCountry() {
        jdbcTemplate.getJdbcTemplate().execute("TRUNCATE TABLE country  CASCADE");
        CountryDto secondCountryDto = CountryDto.builder().id("RU").name("Россия").build();
        assertThrows(EmptyResultDataAccessException.class,
            () -> repository.findById(countryDto.getId()));
        repository.saveOrUpdate(countryDto);
        repository.saveOrUpdate(secondCountryDto);
        List<CountryDto> newFound = repository.findAll();
        assertEquals(2, newFound.size());
        assertEquals(countryDto, newFound.getFirst());
        assertEquals(secondCountryDto, newFound.get(1));
    }

}
