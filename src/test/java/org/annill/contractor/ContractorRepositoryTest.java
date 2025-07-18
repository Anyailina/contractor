package org.annill.contractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.entity.Industry;
import org.annill.contractor.filter.ContractorSearch;
import org.annill.contractor.repository.ContractorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class ContractorRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("contractor")
        .withUsername("myuser")
        .withPassword("secret");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.getJdbcTemplate().execute("TRUNCATE TABLE contractor");
    }

    @Autowired
    private ContractorRepository repository;

    @Test
    void saveOrUpdate_shouldInsertNewContractor() {
        ContractorDto contractorDto = TestData.createContractorDto();

        repository.saveOrUpdate(contractorDto);

        ContractorDto saved = repository.findById(contractorDto.getId());
        assertNotNull(saved);
        assertEquals(contractorDto, saved);
    }

    @Test
    void saveOrUpdate_shouldUpdateExistingContractor() {
        ContractorDto contractorDto = TestData.createContractorDto();

        repository.saveOrUpdate(contractorDto);
        ContractorDto newContractorDto = ContractorDto.builder()
            .id(contractorDto.getId())
            .name("1")
            .nameFull("Общество с ограниченной ответственностью Ромашка")
            .inn("123")
            .ogrn("1027700132195")
            .country("RUS")
            .industry(5)
            .orgForm(1)
            .build();

        repository.saveOrUpdate(newContractorDto);

        ContractorDto updated = repository.findById(contractorDto.getId());
        assertNotNull(updated);
        assertEquals(newContractorDto, updated);
    }

    @Test
    void findById_shouldReturnContractor() {
        ContractorDto contractorDto = TestData.createContractorDto();
        assertThrows(EmptyResultDataAccessException.class,
            () -> repository.findById(contractorDto.getId()));
        repository.saveOrUpdate(contractorDto);
        ContractorDto newFound = repository.findById(contractorDto.getId());
        assertNotNull(newFound);
    }


    @Test
    void logicalDelete_shouldDeactivateContractor() {
        ContractorDto contractorDto = TestData.createContractorDto();

        repository.saveOrUpdate(contractorDto);
        assertNotNull(repository.findById(contractorDto.getId()));
        repository.logicalDelete(contractorDto.getId());

        assertThrows(EmptyResultDataAccessException.class,
            () -> repository.findById(contractorDto.getId()));
    }

    @Test
    void search_shouldFindByFilter() {
        ContractorDto contractorDto = TestData.createContractorDto();
        repository.saveOrUpdate(contractorDto);
        List<ContractorDto> results = repository.search(TestData.createContractorSearch());
        assertEquals(1, results.size());
        assertEquals(contractorDto, results.get(0));
    }

    @Test
    void search_shouldNotFoundBecauseOfId() {
        ContractorDto contractorDto = TestData.createContractorDto();
        Industry industry = Industry.builder().id("5")
            .name("Агропромышленный комплекс и пищевая промышленность (кроме сегментов выделенных отдельно)").build();
        ContractorSearch contractorSearch = ContractorSearch.builder()
            .id("122")
            .searchFilter("Ромашка")
            .country("Российская Федерация")
            .industry(industry)
            .orgForm("-")
            .limit(10)
            .offset(0)
            .build();
        repository.saveOrUpdate(contractorDto);
        List<ContractorDto> results = repository.search(contractorSearch);
        assertEquals(0, results.size());
    }

    @Test
    void search_shouldNotFoundBecauseOfContractorSearch() {
        ContractorDto contractorDto = TestData.createContractorDto();
        Industry industry = Industry.builder().id("5")
            .name("Агропромышленный комплекс и пищевая промышленность (кроме сегментов выделенных отдельно)").build();
        ContractorSearch contractorSearch = ContractorSearch.builder()
            .id("123")
            .searchFilter("122")
            .country("Российская Федерация")
            .industry(industry)
            .orgForm("-")
            .limit(10)
            .offset(0)
            .build();
        repository.saveOrUpdate(contractorDto);
        List<ContractorDto> results = repository.search(contractorSearch);
        assertEquals(0, results.size());
    }

    @Test
    void search_shouldNotFoundBecauseOfCountryName() {
        ContractorDto contractorDto = TestData.createContractorDto();
        Industry industry = Industry.builder().id("5")
            .name("Агропромышленный комплекс и пищевая промышленность (кроме сегментов выделенных отдельно)").build();
        ContractorSearch contractorSearch = ContractorSearch.builder()
            .id("123")
            .searchFilter("Ромашка")
            .country("Россия")
            .industry(industry)
            .orgForm("-")
            .limit(10)
            .offset(0)
            .build();
        repository.saveOrUpdate(contractorDto);
        List<ContractorDto> results = repository.search(contractorSearch);
        assertEquals(0, results.size());
    }

    @Test
    void search_shouldNotFoundBecauseOfIndustry() {
        ContractorDto contractorDto = TestData.createContractorDto();
        Industry industry = Industry.builder().id("5")
            .name("5").build();
        ContractorSearch contractorSearch = ContractorSearch.builder()
            .id("123")
            .searchFilter("Ромашка")
            .country("Российская Федерация")
            .industry(industry)
            .orgForm("-")
            .limit(10)
            .offset(0)
            .build();
        repository.saveOrUpdate(contractorDto);
        List<ContractorDto> results = repository.search(contractorSearch);
        assertEquals(0, results.size());
    }

    @Test
    void search_shouldNotFoundBecauseOfOrgForm() {
        ContractorDto contractorDto = TestData.createContractorDto();
        Industry industry = Industry.builder().id("5")
            .name("Агропромышленный комплекс и пищевая промышленность (кроме сегментов выделенных отдельно)").build();
        ContractorSearch contractorSearch = ContractorSearch.builder()
            .id("123")
            .searchFilter("Ромашка")
            .country("Российская Федерация")
            .industry(industry)
            .orgForm("5")
            .limit(10)
            .offset(0)
            .build();
        repository.saveOrUpdate(contractorDto);
        List<ContractorDto> results = repository.search(contractorSearch);
        assertEquals(0, results.size());
    }
}