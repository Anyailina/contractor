package org.annill.contractor;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.annill.contractor.controller.ContractorController;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.filter.ContractorSearch;
import org.annill.contractor.repository.ContractorRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ContractorController.class)
public class ContractorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractorRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetById() throws Exception {
        ContractorDto contractor = TestData.createContractorDto();
        when(repository.findById("123")).thenReturn(contractor);

        mockMvc.perform(get("/contractor/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is("123")))
            .andExpect(jsonPath("$.name", Matchers.is(("ООО Ромашка"))));
    }

    @Test
    public void testSearch() throws Exception {
        ContractorSearch search = TestData.createContractorSearch();
        ContractorDto contractor = TestData.createContractorDto();
        when(repository.search(search)).thenReturn(List.of(contractor));

        mockMvc.perform(post("/contractor/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(search)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].inn", Matchers.is("7701234567")));
    }

    @Test
    public void testSave() throws Exception {
        ContractorDto contractor = TestData.createContractorDto();

        mockMvc.perform(put("/contractor/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contractor)))
            .andExpect(status().isOk());

        Mockito.verify(repository).saveOrUpdate(contractor);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/contractor/delete/123"))
            .andExpect(status().isOk());

        Mockito.verify(repository).logicalDelete("123");
    }
}