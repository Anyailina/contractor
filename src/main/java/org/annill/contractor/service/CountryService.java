package org.annill.contractor.service;

import lombok.RequiredArgsConstructor;
import org.annill.contractor.converter.CountryConverter;
import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryConverter countryConverter;

    public List<CountryDto> findAll() {
        return countryRepository.findAll().stream().map(countryConverter::toDto).collect(Collectors.toList());
    }

    public CountryDto getById(String id) {
        return countryConverter.toDto(countryRepository.findById(id));
    }

    public void save(CountryDto country) {
        countryRepository.saveOrUpdate(countryConverter.toEntity(country));
    }

    public void delete(String id) {
        countryRepository.logicalDelete(id);
    }

}

