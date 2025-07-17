package org.annill.contractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.annill.contractor.converter.CountryConverter;
import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.entity.Country;
import org.annill.contractor.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ExtendWith(MockitoExtension.class)
public class CountryRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Mock
    private CountryConverter countryConverter;

    @InjectMocks
    private CountryRepository countryRepository;

    private Country testCountry;
    private CountryDto testCountryDto;

    @BeforeEach
    public void setUp() {
        testCountry = Country.builder()
            .id("1")
            .name("Russia")
            .isActive(true)
            .build();

        testCountryDto = CountryDto.builder()
            .id("1")
            .name("Russia")
            .build();
    }

    @Test
    void testFindAll_ShouldReturnListOfCountries() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
            .thenReturn(Collections.singletonList(testCountry));
        when(countryConverter.toDto(testCountry)).thenReturn(testCountryDto);

        List<CountryDto> result = countryRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCountryDto.getId(), result.get(0).getId());
    }

    @Test
    void testFindById_ShouldReturnCountryDto() {
        when(jdbcTemplate.queryForObject(eq(CountyQuery.FIND_BY_ID_QUERY),
            anyMap(), any(RowMapper.class))).thenReturn(testCountry);
        when(countryConverter.toDto(testCountry)).thenReturn(testCountryDto);

        CountryDto result = countryRepository.findById("1");

        assertNotNull(result);
        assertEquals(testCountryDto.getId(), result.getId());
        assertEquals(testCountryDto.getName(), result.getName());
    }

    @Test
    void testFindById_WhenIdIsBlank_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> countryRepository.findById(""));
    }

    @Test
    void testFindById_WhenCountryNotFound_ShouldThrowException() {
        when(jdbcTemplate.queryForObject(eq(CountyQuery.FIND_BY_ID_QUERY),
            anyMap(), any(RowMapper.class))).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> countryRepository.findById("999"));
    }

    @Test
    void testSaveOrUpdate_WhenCountryExists_ShouldUpdate() {
        when(jdbcTemplate.queryForObject(eq(CountyQuery.COUNT_BY_ID_QUERY),
            anyMap(), eq(Integer.class))).thenReturn(1);

        countryRepository.saveOrUpdate(testCountry);

        verify(jdbcTemplate).update(eq(CountyQuery.UPDATE_QUERY), anyMap());
    }

    @Test
    void testSaveOrUpdate_WhenCountryNotExists_ShouldInsert() {
        when(jdbcTemplate.queryForObject(eq(CountyQuery.COUNT_BY_ID_QUERY),
            anyMap(), eq(Integer.class))).thenReturn(0);

        countryRepository.saveOrUpdate(testCountry);

        verify(jdbcTemplate).update(eq(CountyQuery.INSERT_QUERY), anyMap());
    }

    @Test
    void testLogicalDelete_ShouldCallUpdate() {
        when(jdbcTemplate.update(eq(CountyQuery.LOGICAL_DELETE_QUERY), anyMap()))
            .thenReturn(1);

        countryRepository.logicalDelete("1");

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(jdbcTemplate).update(eq(CountyQuery.LOGICAL_DELETE_QUERY), captor.capture());
        assertEquals("1", captor.getValue().get("id"));
    }

    @Test
    void testLogicalDelete_WhenIdIsBlank_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> countryRepository.logicalDelete(""));
    }

    @Test
    void testLogicalDelete_WhenCountryNotFound_ShouldThrowException() {
        when(jdbcTemplate.update(eq(CountyQuery.LOGICAL_DELETE_QUERY), anyMap()))
            .thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> countryRepository.logicalDelete("999"));
    }
}