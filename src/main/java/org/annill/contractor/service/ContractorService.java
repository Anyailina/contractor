package org.annill.contractor.service;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.annill.contractor.converter.ContractorConverter;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.filter.ContractorSearch;
import org.annill.contractor.repository.ContractorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContractorService {

    private final ContractorRepository contractorRepository;
    private final ContractorConverter contractorConverter;
    private static final  String ROLE = "CONTRACTOR_RUS";

    public void saveOrUpdate(ContractorDto contractorDto) {
        if (contractorDto == null || contractorDto.getId() == null) {
            throw new EntityNotFoundException();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", contractorDto.getId());
        params.put("parent_id", contractorDto.getParentId());
        params.put("name", contractorDto.getName());
        params.put("name_full", contractorDto.getNameFull());
        params.put("inn", contractorDto.getInn());
        params.put("ogrn", contractorDto.getOgrn());
        params.put("country", contractorDto.getCountry());
        params.put("industry", contractorDto.getIndustry());
        params.put("orgForm", contractorDto.getOrgForm());

        contractorRepository.saveOrUpdate(params, contractorDto.getId());

    }

    public List<ContractorDto> filterRusSearch(ContractorSearch contractorSearch, Authentication authentication) {
        boolean isSearchRus = hasAuthority(authentication);

        if (isSearchRus) {
            return search(contractorSearch, ROLE);
        }
        return search(contractorSearch);

    }

    private boolean hasAuthority(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream().anyMatch(a -> ROLE.equals(a.getAuthority()));
    }

    public ContractorDto findById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID контрагента не может быть пустым");
        }
        return contractorConverter.toDto(contractorRepository.findById(id));
    }

    @Transactional
    public void logicalDelete(String id) {
        findById(id);
        contractorRepository.logicalDelete(id);
    }

    public List<ContractorDto> search(ContractorSearch contractorSearch) {
        return search(contractorSearch, null);
    }

    public List<ContractorDto> search(ContractorSearch contractorSearch, @Nullable String idCountry) {

        return contractorRepository.search(contractorSearch, idCountry).stream()
                .map(contractorConverter::toDto)
                .collect(Collectors.toList());
    }

}
