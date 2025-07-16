package org.annill.contractor.converter;

import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.entity.Country;
import org.springframework.stereotype.Component;

@Component
public class CountryConverter {

    public CountryDto toDto(Country country) {
        return new CountryDto(country.getId(), country.getName());
    }

    public Country toDto(CountryDto countryDto) {
        return Country.builder().id(countryDto.getId()).name(countryDto.getName()).build();
    }

}
