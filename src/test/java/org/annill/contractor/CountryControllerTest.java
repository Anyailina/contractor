package org.annill.contractor;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.annill.contractor.controller.CountryController;
import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.entity.Country;
import org.annill.contractor.repository.CountryRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CountryController.class)
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private CountryDto createTestCountryDto() {
        return CountryDto.builder()
            .id("1")
            .name("Russia")
            .build();
    }

    private Country createTestCountry() {
        return Country.builder()
            .id("1")
            .name("Russia")
            .build();
    }

    @Test
    public void testGetAll() throws Exception {
        CountryDto country = createTestCountryDto();
        when(repository.findAll()).thenReturn(List.of(country));

        mockMvc.perform(get("/country/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", Matchers.is("1")))
            .andExpect(jsonPath("$[0].name", Matchers.is("Russia")));
    }

    @Test
    public void testGetById() throws Exception {
        CountryDto country = createTestCountryDto();
        when(repository.findById("1")).thenReturn(country);

        mockMvc.perform(get("/country/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is("1")))
            .andExpect(jsonPath("$.name", Matchers.is("Russia")));
    }

    @Test
    public void testSave() throws Exception {
        Country country = createTestCountry();

        mockMvc.perform(put("/country/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(country)))
            .andExpect(status().isOk());

        Mockito.verify(repository).saveOrUpdate(country);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/country/delete/1"))
            .andExpect(status().isOk());

        Mockito.verify(repository).logicalDelete("1");
    }
}