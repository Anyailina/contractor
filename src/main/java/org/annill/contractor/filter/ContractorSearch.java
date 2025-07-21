package org.annill.contractor.filter;

import jdk.jfr.Name;
import lombok.Builder;
import lombok.Value;
import org.annill.contractor.entity.Industry;

@Value
@Builder(toBuilder = true)
public class ContractorSearch {

    @Name("contractor_id")
    private String id;
    @Name("parent_id")
    private String parentId;
    @Name("contractor_search")
    private String searchFilter;
    private String country;
    private Industry industry;
    @Name("org_form")
    private String orgForm;
    private Integer limit;
    private Integer offset;

}
