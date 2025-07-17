package org.annill.contractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.annill.contractor.controller.ContractorController;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.entity.Contractor;
import org.annill.contractor.repository.ContractorRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class ContractorApplicationTests {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("contractor")
        .withUsername("myuser")
        .withPassword("secret");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ContractorRepository repository;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private ContractorController controller;

    private String testContractorId;
    private String baseUrl;


    @BeforeEach
    void setUpBeforeEach() {
        testContractorId = "123456789012";
        baseUrl = "http://localhost:" + port + "/contractor/";

        Contractor contractor = Contractor.builder()
            .id(testContractorId)
            .parentId("1")
            .name("Test Contractor")
            .nameFull("Test Contractor Full Name")
            .inn("1234567890")
            .ogrn("1234567890123")
            .country("RUS")
            .industry(1)
            .orgForm(2)
            .build();
        save(contractor);
    }

    @AfterEach
    void setUpAfterEach() {
        delete(testContractorId);
    }


    @Test
    void getByCorrectId_AtRepositoryLevel() {
        ContractorDto found = repository.findById(testContractorId);
        assertNotNull(found);
        assertEquals("Test Contractor", found.getName());
        assertEquals("Test Contractor Full Name", found.getNameFull());
    }

    @Test
    void getByCorrectId_AtControllerLevel_ShouldReturn200() {
        ResponseEntity<ContractorDto> getResponse = restTemplate.getForEntity(
            baseUrl + testContractorId,
            ContractorDto.class
        );
        assertNotNull(getResponse);
        assertEquals(200, getResponse.getStatusCodeValue());
        assertEquals("Test Contractor", getResponse.getBody().getName());
    }


    @Test
    void findByIncorrectId_AtRepositoryLevel() {
        assertThrows(EmptyResultDataAccessException.class, () -> repository.findById("10"));

        assertThrows(NullPointerException.class, () -> repository.findById(null));

        assertThrows(EmptyResultDataAccessException.class, () -> repository.findById(""));

        assertThrows(EmptyResultDataAccessException.class, () -> repository.findById("   "));
    }

    @Test
    void getById_WithNonExistentId_ShouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "100",
            String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getById_WithNullId_ShouldReturn400() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + " ",
            String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getById_WithEmptyPath_ShouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl,
            String.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void deleteByCorrectId_AtRepositoryLevel() {
        repository.logicalDelete(testContractorId);
        assertThrows(EmptyResultDataAccessException.class, () -> repository.findById(testContractorId));
    }


    @Test
    void deleteByIncorrectId_AtRepositoryLevel() {
        assertThrows(EmptyResultDataAccessException.class, () -> repository.logicalDelete("10"));

        assertThrows(NullPointerException.class, () -> repository.logicalDelete(null));

        assertThrows(EmptyResultDataAccessException.class, () -> repository.logicalDelete(""));

        assertThrows(EmptyResultDataAccessException.class, () -> repository.logicalDelete("   "));
    }

    @Test
    void testUpdateContractor_AtControllerLevel() {
        ContractorDto updated = ContractorDto.builder()
            .id(testContractorId)
            .name("Updated Contractor")
            .nameFull("Updated Contractor Full Name")
            .inn("1234567890")
            .ogrn("1234567890123")
            .country("RUS")
            .industry(1)
            .orgForm(1)
            .build();

        restTemplate.put(baseUrl + "/save", updated);

        ResponseEntity<ContractorDto> getResponse = restTemplate.getForEntity(
            baseUrl + "/" + testContractorId,
            ContractorDto.class
        );

        assertEquals(updated, getResponse.getBody());
    }

    @Test
    void testUpdateContractor_AtRepositoryLevel() {
        ContractorDto updated = ContractorDto.builder()
            .id(testContractorId)
            .name("Updated Contractor")
            .nameFull("Updated Contractor Full Name")
            .inn("1234567890")
            .ogrn("1234567890123")
            .country("RUS")
            .industry(1)
            .orgForm(1)
            .build();

        repository.saveOrUpdate(updated);
        ResponseEntity<ContractorDto> newControllerDto = controller.getById(testContractorId);
        assertEquals(updated, newControllerDto.getBody());
    }

    @Test
    void testSaveContractor_AtRepositoryLevel() {
        ContractorDto updated = ContractorDto.builder()
            .id("5")
            .name("Contractor")
            .nameFull("Full Name")
            .inn("324")
            .ogrn("4545")
            .country("RUS")
            .industry(1)
            .orgForm(1)
            .build();

        repository.saveOrUpdate(updated);
        ContractorDto newControllerDto = repository.findById("5");
        assertEquals(updated, newControllerDto);
    }

    @Test
    void testSaveIncorrectContractor_AtRepositoryLevel() {
        ContractorDto updated = ContractorDto.builder()
            .id("5")
            .name("Contractor")
            .nameFull("Full Name")
            .inn("324")
            .ogrn("4545")
            .country("RU")
            .industry(1)
            .orgForm(1)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveOrUpdate(updated));
    }

    @Test
    void testSaveIncorrectContractor_AtControllerLevel() {
        ContractorDto updated = ContractorDto.builder()
            .id("5")
            .name("Contractor")
            .nameFull("Full Name")
            .inn("324")
            .ogrn("4545")
            .country("RU")
            .industry(1)
            .orgForm(1)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveOrUpdate(updated));
    }

    @Test
    void testSaveContractor_AtControllerLevel() {
        ContractorDto updated = ContractorDto.builder()
            .id("5")
            .name("Contractor")
            .nameFull("Full Name")
            .inn("324")
            .ogrn("4545")
            .country("RUS")
            .industry(1)
            .orgForm(1)
            .build();
        restTemplate.put(
            baseUrl + "/save",
            updated
        );

        ResponseEntity<ContractorDto> newControllerDto = controller.getById("5");
        assertEquals(updated, newControllerDto.getBody());
    }

    @Test
    void testSaveNullContractor_AtRepositoryLevel() {
        assertThrows(EntityNotFoundException.class, () -> repository.saveOrUpdate(null));
    }

    public void save(Contractor contractor) {
        String sql = """
            INSERT INTO contractor 
            (id, name, name_full, inn, ogrn, country, industry, org_form, create_date, is_active)
            VALUES 
            (:id, :name, :nameFull, :inn, :ogrn, :country, :industry, :orgForm, now(), true)
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", contractor.getId());
        params.put("name", contractor.getName());
        params.put("nameFull", contractor.getNameFull());
        params.put("inn", contractor.getInn());
        params.put("ogrn", contractor.getOgrn());
        params.put("country", contractor.getCountry());
        params.put("industry", contractor.getIndustry());
        params.put("orgForm", contractor.getOrgForm());

        jdbcTemplate.update(sql, params);
    }

    public void delete(String id) {
        String sql = """
            DELETE from contractor where id = :id
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        jdbcTemplate.update(sql, params);
    }

}