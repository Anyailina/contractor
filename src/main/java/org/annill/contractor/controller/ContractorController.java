package org.annill.contractor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.annill.contractor.ContractorSearch;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.repository.ContractorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер контрагента
 *
 * @author anailina
 */
@RestController
@Slf4j
@RequestMapping("/contractor")
@Tag(name = "Contractor API", description = "Управление контрагентами")
public class ContractorController {

    private final ContractorRepository repository;

    public ContractorController(ContractorRepository repository) {
        this.repository = repository;
    }

    @PutMapping("/save")
    @Operation(summary = "Сохранение контрагента")
    public void save(@RequestBody ContractorDto contractorDto) {
        log.info("Сохранение контрагента");
        repository.saveOrUpdate(contractorDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск контрагента по id")
    public ResponseEntity<ContractorDto> getById(@PathVariable String id) {
        log.info("Поиск контрагента по id");
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Контрагент с id {} не найден", id);
                return ResponseEntity.notFound().build();
            });
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление контрагента по id")
    public void delete(@PathVariable String id) {
        log.info("Удаление контрагента по id");
        repository.logicalDelete(id);
    }

    @PostMapping("/search")
    @Operation(summary = "Поиск контрагента по фильтру")
    public List<ContractorDto> search(
        @RequestBody ContractorSearch contractorSearch
    ) {
        log.info("Поиск контрагента по фильтру");
        return repository.search(contractorSearch);
    }

}
