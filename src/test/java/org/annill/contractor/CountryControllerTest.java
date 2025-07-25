package org.annill.contractor;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.annill.contractor.controller.CountryController;
import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.repository.CountryRepository;
import org.annill.contractor.security.AuthTokenFilter;
import org.annill.contractor.service.CountryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(CountryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryService countryService;

    @MockitoBean
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private ObjectMapper objectMapper;


    private String json;
    private CountryDto countryDto;

    @BeforeEach
    void setUp() throws IOException {
        json = Files.readString(Paths.get("src/test/resources/data/country.json"));

        countryDto = objectMapper.readValue(json, CountryDto.class);
    }

    @Test
    public void testGetAll() throws Exception {
        when(countryService.findAll()).thenReturn(List.of(countryDto));

        MvcResult result = mockMvc.perform(get("/country/all"))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        List<CountryDto> actualList = objectMapper.readValue(
            responseBody,
            objectMapper.getTypeFactory().constructCollectionType(List.class, CountryDto.class)
        );

        Assertions.assertEquals(List.of(countryDto), actualList);

    }

    @Test
    public void testGetById() throws Exception {
        when(countryService.getById(countryDto.getId())).thenReturn(countryDto);

        MvcResult result = mockMvc.perform(get(String.format("/country/%s", countryDto.getId())))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        CountryDto newCountryDto = objectMapper.readValue(responseBody, CountryDto.class);

        Assertions.assertEquals(countryDto, newCountryDto);
    }

    @Test
    public void testWrongGetId() throws Exception {
        when(countryService.getById(countryDto.getId())).thenThrow(new DataIntegrityViolationException("wrong"));

        mockMvc.perform(get(String.format("/country/%s", countryDto.getId())))
            .andExpect(status().isNotFound());

    }

    @Test
    public void testWrongDelete() throws Exception {
        doThrow(new EmptyResultDataAccessException(1))
            .when(countryService).delete(countryDto.getId());


        mockMvc.perform(delete(String.format("/country/delete/%s", countryDto.getId())))
            .andExpect(status().isNotFound());

    }

}
