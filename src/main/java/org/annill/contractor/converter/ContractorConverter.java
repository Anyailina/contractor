package org.annill.contractor.converter;

import java.time.LocalDateTime;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.entity.Contractor;
import org.springframework.stereotype.Component;

@Component
public class ContractorConverter {

    public ContractorDto toDto(Contractor entity) {
        return new ContractorDto(
            entity.getId(),
            entity.getParentId(),
            entity.getName(),
            entity.getNameFull(),
            entity.getInn(),
            entity.getOgrn(),
            entity.getCountry(),
            entity.getIndustry(),
            entity.getOrgForm()
        );
    }

    public Contractor toEntity(ContractorDto dto,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String createUserId,
        String modifyUserId,
        boolean isActive) {
        return Contractor.builder()
            .id(dto.getId())
            .parentId(dto.getParentId())
            .name(dto.getName())
            .nameFull(dto.getNameFull())
            .inn(dto.getInn())
            .ogrn(dto.getOgrn())
            .country(dto.getCountry())
            .industry(dto.getIndustry())
            .orgForm(dto.getOrgForm())
            .createDate(createDate)
            .modifyDate(modifyDate)
            .createUserId(createUserId)
            .modifyUserId(modifyUserId)
            .isActive(isActive)
            .build();
    }

}
