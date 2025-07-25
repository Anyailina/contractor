package org.annill.contractor.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.annill.contractor.TestData;

import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.filter.ContractorSearch;
import org.annill.contractor.security.JwtUtils;
import org.annill.contractor.service.ContractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UiContractorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractorService service;

    @MockitoBean
    private JwtUtils jwtUtils;


    @Autowired
    private ObjectMapper objectMapper;

    private ContractorDto contractorDto;
    private ContractorSearch contractorSearch;

    private final String VALID_TOKEN = "valid";
    private final String INVALID_TOKEN = "invalid";

    @BeforeEach
    void setup() {
        contractorDto = TestData.createContractorDto();
        contractorSearch = TestData.createContractorSearch();

        when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(VALID_TOKEN)).thenReturn("user");
        when(jwtUtils.getRoles(VALID_TOKEN)).thenReturn(List.of("CONTRACTOR_SUPERUSER"));

        when(jwtUtils.validateJwtToken(INVALID_TOKEN)).thenReturn(false);
    }

    @Test
    void testSave_Unauthorized() throws Exception {
        mockMvc.perform(put("/ui/contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractorDto)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testDelete_Unauthorized() throws Exception {
        mockMvc.perform(delete("/ui/contractor/delete/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDelete_AccessGranted() throws Exception {
        String id = UUID.randomUUID().toString();
        doNothing().when(service).logicalDelete(id);

        mockMvc.perform(delete("/ui/contractor/delete/" + id)
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById_Unauthorized() throws Exception {
        mockMvc.perform(get("/ui/contractor/" + contractorDto.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetById_AccessGranted() throws Exception {
        when(service.findById(contractorDto.getId())).thenReturn(contractorDto);
        System.out.println(contractorDto.getId());
        mockMvc.perform(get("/ui/contractor/" + contractorDto.getId())
                        .header("Authorization", "Bearer " + VALID_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSearch_Unauthorized() throws Exception {
        mockMvc.perform(post("/ui/contractor/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractorSearch)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSearch_AccessGranted() throws Exception {
        when(service.filterRusSearch(any(), any())).thenReturn(List.of(contractorDto));

        mockMvc.perform(post("/ui/contractor/search")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractorSearch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
