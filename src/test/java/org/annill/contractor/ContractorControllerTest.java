package org.annill.contractor;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.annill.contractor.controller.ContractorController;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.filter.ContractorSearch;
import org.annill.contractor.repository.ContractorRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(ContractorController.class)
public class ContractorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractorRepository repository;

    @Autowired
    private ObjectMapper objectMapper;


    private String jsonSearch;
    private String jsonResponse;
    private ContractorSearch search;
    private ContractorDto expected;

    @BeforeEach
    void setUp() throws IOException {
        jsonSearch = Files.readString(Paths.get("src/test/resources/data/contractor_search.json"));
        jsonResponse = Files.readString(Paths.get("src/test/resources/data/contractor.json"));

        search = objectMapper.readValue(jsonSearch, ContractorSearch.class);
        expected = objectMapper.readValue(jsonResponse, ContractorDto.class);
    }

    @Test
    public void testGetById() throws Exception {
        when(repository.findById(expected.getId())).thenReturn(expected);

        mockMvc.perform(get(String.format("/contractor/%s", expected.getId()))).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is("123")))
            .andExpect(jsonPath("$.name", Matchers.is(("ООО Ромашка"))));
    }

    @Test
    public void testSearch() throws Exception {

        when(repository.search(search)).thenReturn(List.of(expected));

        MvcResult result = mockMvc.perform(
                post("/contractor/search").contentType(MediaType.APPLICATION_JSON).content(jsonSearch))
            .andExpect(status().isOk()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        List<ContractorDto> actualList = objectMapper.readValue(responseBody,
            objectMapper.getTypeFactory().constructCollectionType(List.class, ContractorDto.class));

        Assertions.assertEquals(List.of(expected), actualList);
    }

    @Test
    public void testWrongGetId() throws Exception {
        when(repository.findById(expected.getId())).thenThrow(new DataIntegrityViolationException("wrong"));

        mockMvc.perform(get(String.format("/contractor/%s", expected.getId()))).andExpect(status().isNotFound());

    }

}
