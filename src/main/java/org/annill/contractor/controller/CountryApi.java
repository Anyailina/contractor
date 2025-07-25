package org.annill.contractor.controller;

import java.util.List;
import org.annill.contractor.dto.CountryDto;
import org.springframework.http.ResponseEntity;

public interface CountryApi {

    ResponseEntity<List<CountryDto>> findAll();

    ResponseEntity<CountryDto> getById(String id);

    ResponseEntity<Void> save(CountryDto country);

    ResponseEntity<Void> delete(String id);

}
