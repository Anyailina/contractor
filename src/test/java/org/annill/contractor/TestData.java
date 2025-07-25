package org.annill.contractor;

import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.entity.Contractor;
import org.annill.contractor.entity.Industry;
import org.annill.contractor.filter.ContractorSearch;

public class TestData {

    public static ContractorDto createContractorDto() {
        return ContractorDto.builder().id("123").name("ООО Ромашка")
            .nameFull("Общество с ограниченной ответственностью Ромашка").inn("7701234567").ogrn("1027700132195")
            .country("RUS").industry(5).orgForm(1).build();
    }

    public static Contractor createContractor() {
        return Contractor.builder().id("123").name("ООО Ромашка")
                .nameFull("Общество с ограниченной ответственностью Ромашка").inn("7701234567").ogrn("1027700132195")
                .country("RUS").industry(5).orgForm(1).build();
    }

    public static ContractorSearch createContractorSearch() {
        Industry industry = Industry.builder().id("5")
            .name("Агропромышленный комплекс и пищевая промышленность (кроме сегментов выделенных отдельно)").build();
        return ContractorSearch.builder().id("123").searchFilter("Ромашка").country("Российская Федерация")
            .industry(industry).orgForm("-").limit(10).offset(0).build();
    }

}

