package org.annill.contractor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.filter.ContractorSearch;
import org.annill.contractor.service.ContractorService;
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
@RequiredArgsConstructor
public class ContractorController implements ContractorApi {

    private final ContractorService service;

    @PutMapping("/save")
    @Operation(summary = "Сохранение контрагента")
    public ResponseEntity<?> save(@RequestBody ContractorDto contractorDto) {
        log.info("Сохранение контрагента");
        service.saveOrUpdate(contractorDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск контрагента по id")
    public ResponseEntity<ContractorDto> getById(@PathVariable String id) {
        log.info("Поиск контрагента по id");
        ContractorDto contractor = service.findById(id);
        return ResponseEntity.ok(contractor);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление контрагента по id")
    public ResponseEntity<?> delete(@PathVariable String id) {
        log.info("Удаление контрагента по id");
        service.logicalDelete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    @Operation(summary = "Поиск контрагента по фильтру")
    public ResponseEntity<List<ContractorDto>> search(@RequestBody ContractorSearch contractorSearch) {
        log.info("Поиск контрагента по фильтру");
        return ResponseEntity.ok(service.search(contractorSearch));
    }

}
