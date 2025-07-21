package org.annill.contractor.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CountryDto {

    private String id;
    private String name;

}
