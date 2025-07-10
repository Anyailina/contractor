package org.annill.contractor.entity;

import lombok.Data;

@Data

public class Contractor {

    private String id;
    private String parentId;
    private String name;
    private String nameFull;
    private String inn;
    private String ogrn;
    private String countryId;
    private Integer industryId;
    private Integer orgFormId;
    private boolean isActive;

}
