package org.annill.contractor.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.annill.contractor.entity.Contractor;
import org.annill.contractor.repository.ContractorRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер контрагента
 */

@RestController
@Slf4j
@RequestMapping("/contractor")

public class ContractorController {

    private final ContractorRepository repository;

    public ContractorController(ContractorRepository repository) {
        this.repository = repository;
    }

    @PutMapping("/save")
    public void save(@RequestBody Contractor contractor) {
        log.info("Сохранение контрагента");
        repository.saveOrUpdate(contractor);
    }

    @GetMapping("/{id}")
    public Contractor getById(@PathVariable String id) {
        log.info("Поиск контрагента по id");
        return repository.findById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        log.info("Удаление контрагента по id");
        repository.logicalDelete(id);
    }

    @PostMapping("/search")
    public List<Contractor> search(
        @RequestParam String contractorId,
        @RequestParam String parentId,
        @RequestParam String contractorSearch,
        @RequestParam String country,
        @RequestParam Integer industry,
        @RequestParam String orgForm,
        @RequestParam int limit,
        @RequestParam int offset
    ) {
        log.info("Поиск контрагента по фильтру");
        return repository.search(contractorId, parentId, contractorSearch, country, industry, orgForm, limit, offset);
    }

}
