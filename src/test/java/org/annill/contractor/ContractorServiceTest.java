package org.annill.contractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.entity.Contractor;
import org.annill.contractor.entity.Industry;
import org.annill.contractor.filter.ContractorSearch;
import org.annill.contractor.repository.ContractorRepository;
import org.annill.contractor.security.AuthTokenFilter;
import org.annill.contractor.service.ContractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ContractorServiceTest {

    @MockitoBean
    private AuthTokenFilter authTokenFilter;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
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
    private final String ROLE = "CONTRACTOR_RUS";

    ContractorDto contractorDto = TestData.createContractorDto();

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.getJdbcTemplate().execute("TRUNCATE TABLE contractor");
    }

    @Autowired
    private ContractorService contractorService;

    @Test
    @Rollback
    void saveOrUpdate_shouldInsertNewContractor() {

        contractorService.saveOrUpdate(contractorDto);

        ContractorDto saved = contractorService.findById(contractorDto.getId());
        assertNotNull(saved);
        assertEquals(contractorDto, saved);
    }

    @Test
    @Rollback
    void saveOrUpdate_shouldUpdateExistingContractor() {

        contractorService.saveOrUpdate(contractorDto);
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

        contractorService.saveOrUpdate(newContractorDto);

        ContractorDto updated = contractorService.findById(contractorDto.getId());
        assertNotNull(updated);
        assertEquals(newContractorDto, updated);
    }

    @Test
    void findById_shouldReturnContractor() {
        assertThrows(EmptyResultDataAccessException.class,
            () -> contractorService.findById(contractorDto.getId()));
        contractorService.saveOrUpdate(contractorDto);
        ContractorDto newFound = contractorService.findById(contractorDto.getId());
        assertNotNull(newFound);
    }


    @Test
    @Rollback
    void logicalDelete_shouldDeactivateContractor() {
        contractorService.saveOrUpdate(contractorDto);
        assertNotNull(contractorService.findById(contractorDto.getId()));
        contractorService.logicalDelete(contractorDto.getId());

        assertThrows(EmptyResultDataAccessException.class,
            () -> contractorService.findById(contractorDto.getId()));
    }

    @Test
    void search_shouldFindByFilter() {
        contractorService.saveOrUpdate(contractorDto);
        List<ContractorDto> results = contractorService.search(TestData.createContractorSearch());
        assertEquals(1, results.size());
        assertEquals(contractorDto, results.get(0));
    }

    @Test
    void search_shouldNotFoundBecauseOfId() {
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
        contractorService.saveOrUpdate(contractorDto);
        List<ContractorDto> results = contractorService.search(contractorSearch);
        assertEquals(0, results.size());
    }

    @Test
    void search_shouldNotFoundBecauseOfContractorSearch() {
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
        contractorService.saveOrUpdate(contractorDto);
        List<ContractorDto> results = contractorService.search(contractorSearch);
        assertEquals(0, results.size());
    }

    @Test
    void search_shouldNotFoundBecauseOfCountryName() {
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
        contractorService.saveOrUpdate(contractorDto);
        List<ContractorDto> results = contractorService.search(contractorSearch);
        assertEquals(0, results.size());
    }

    @Test
    void search_shouldNotFoundBecauseOfIndustry() {
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
        contractorService.saveOrUpdate(contractorDto);
        List<ContractorDto> results = contractorService.search(contractorSearch);
        assertEquals(0, results.size());
    }

    @Test
    void search_shouldNotFoundBecauseOfOrgForm() {
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
        contractorService.saveOrUpdate(contractorDto);
        List<ContractorDto> results = contractorService.search(contractorSearch);
        assertEquals(0, results.size());
    }

}
