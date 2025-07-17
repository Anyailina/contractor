package org.annill.contractor;


import static org.annill.contractor.TestData.createContractor;
import static org.annill.contractor.TestData.createContractorDto;
import static org.annill.contractor.TestData.createContractorSearch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.annill.contractor.converter.ContractorConverter;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.entity.Contractor;
import org.annill.contractor.repository.ContractorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ExtendWith(MockitoExtension.class)
public class ContractorRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Mock
    private ContractorConverter contractorConverter;

    @InjectMocks
    private ContractorRepository contractorRepository;

    private ContractorDto testContractorDto;
    private Contractor testContractor;
    private ContractorSearch testContractorSearch;

    @BeforeEach
    public void setUp() {
        testContractorDto = createContractorDto();
        testContractorSearch = createContractorSearch();
        testContractor = createContractor();
    }


    @Test
    void testSaveOrUpdate_WhenContractorExists_ShouldUpdate() {
        when(jdbcTemplate.queryForObject(eq(Query.COUNT_BY_ID_SQL),
            anyMap(), eq(Integer.class))).thenReturn(1);

        contractorRepository.saveOrUpdate(testContractorDto);

        verify(jdbcTemplate).update(eq(Query.UPDATE_CONTRACTOR_SQL), anyMap());
    }

    @Test
    void testSaveOrUpdate_WhenContractorNotExists_ShouldInsert() {
        when(jdbcTemplate.queryForObject(eq(Query.COUNT_BY_ID_SQL),
            anyMap(), eq(Integer.class))).thenReturn(0);

        contractorRepository.saveOrUpdate(testContractorDto);

        verify(jdbcTemplate).update(eq(Query.INSERT_CONTRACTOR_SQL), anyMap());
    }

    @Test
    void testSaveOrUpdate_WhenContractorDtoIsNull_ShouldThrowException() {
        assertThrows(EntityNotFoundException.class, () -> contractorRepository.saveOrUpdate(null));
    }

    @Test
    void testFindById_ShouldReturnContractorDto() {
        when(jdbcTemplate.queryForObject(eq(Query.SELECT_BY_ID_SQL),
            anyMap(), any(RowMapper.class))).thenReturn(testContractor);
        when(contractorConverter.toDto(testContractor)).thenReturn(testContractorDto);

        ContractorDto result = contractorRepository.findById("123");

        assertNotNull(result);
        assertEquals(testContractorDto.getId(), result.getId());
        assertEquals(testContractorDto.getName(), result.getName());
    }

    @Test
    void testLogicalDelete_ShouldCallUpdate() {
        when(jdbcTemplate.queryForObject(eq(Query.SELECT_BY_ID_SQL),
            anyMap(), any(RowMapper.class))).thenReturn(testContractor);

        contractorRepository.logicalDelete("123");

        verify(jdbcTemplate).update(eq(Query.LOGICAL_DELETE_SQL),
            eq(Map.of("id", "123")));
    }

    @Test
    void testSearch_WithAllParameters_ShouldReturnFilteredResults() {
        List<Contractor> contractors = List.of(testContractor);

        when(jdbcTemplate.query(anyString(), anyMap(), any(RowMapper.class)))
            .thenReturn(contractors);
        when(contractorConverter.toDto(testContractor)).thenReturn(testContractorDto);

        List<ContractorDto> result = contractorRepository.search(testContractorSearch);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testContractorDto.getId(), result.get(0).getId());
    }

    @Test
    void testSearch_WithEmptyResult_ShouldReturnEmptyList() {
        when(jdbcTemplate.query(anyString(), anyMap(), any(RowMapper.class)))
            .thenReturn(Collections.emptyList());

        List<ContractorDto> result = contractorRepository.search(testContractorSearch);

        assertTrue(result.isEmpty());
    }
}