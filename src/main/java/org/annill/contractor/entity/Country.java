package org.annill.contractor.entity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Country {

    private String id;
    private String name;
    private boolean isActive;

}
