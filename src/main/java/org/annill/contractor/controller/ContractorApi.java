package org.annill.contractor.controller;

import java.util.List;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.filter.ContractorSearch;
import org.springframework.http.ResponseEntity;

public interface ContractorApi {

    ResponseEntity<?> save(ContractorDto contractorDto);

    ResponseEntity<ContractorDto> getById(String id);

    ResponseEntity<?> delete(String id);

    ResponseEntity<List<ContractorDto>> search(ContractorSearch contractorSearch);

}
