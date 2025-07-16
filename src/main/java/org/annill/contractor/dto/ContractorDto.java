package org.annill.contractor.dto;

import lombok.Value;

@Value
public class ContractorDto {

    private String id;
    private String parentId;
    private String name;
    private String nameFull;
    private String inn;
    private String ogrn;
    private String country;
    private Integer industry;
    private Integer orgForm;

}
