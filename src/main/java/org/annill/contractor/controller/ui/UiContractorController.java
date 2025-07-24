package org.annill.contractor.controller.ui;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.annill.contractor.dto.ContractorDto;
import org.annill.contractor.filter.ContractorSearch;
import org.annill.contractor.repository.ContractorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления контрагентами через пользовательский интерфейс. Предоставляет API для создания, поиска,
 * изменения и удаления контрагентов. Требует авторизации с соответствующими ролями.
 *
 * @author ailina
 */
@RestController
@Slf4j
@RequestMapping("ui/contractor")
@RequiredArgsConstructor
public class UiContractorController {

    private final ContractorRepository contractorRepository;

    /**
     * Сохраняет контрагента в системе.
     *
     * @param contractorDto DTO контрагента для сохранения (тело запроса в формате JSON)
     * @return ResponseEntity с результатом операции
     */
    @PutMapping("/save")
    @PreAuthorize("hasAnyRole('CONTRACTOR_SUPERUSER','SUPERUSER')")
    @Operation(summary = "Сохранение контрагента")
    public ResponseEntity<?> save(@RequestBody ContractorDto contractorDto) {
        log.info("Сохранение контрагента");
        contractorRepository.saveOrUpdate(contractorDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Возвращает контрагента по его идентификатору.
     *
     * @param id UUID контрагента (путь URL)
     * @return ResponseEntity с данными контрагента (ContractorDto)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Поиск контрагента по id")
    @PreAuthorize("hasAnyRole('USER','CONTRACTOR_RUS,CONTRACTOR_SUPERUSER','SUPERUSER')")
    public ResponseEntity<ContractorDto> getById(@PathVariable String id) {
        log.info("Поиск контрагента по id");
        return ResponseEntity.ok(contractorRepository.findById(id));
    }

    /**
     * Удаляет контрагента по идентификатору.
     *
     * @param id UUID контрагента (путь URL)
     * @return ResponseEntity с результатом операции
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление контрагента по id")
    @PreAuthorize("hasAnyRole('CONTRACTOR_SUPERUSER')")
    public ResponseEntity<?> delete(@PathVariable String id) {
        log.info("Удаление контрагента по id");
        contractorRepository.logicalDelete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Осуществляет поиск контрагентов по заданным критериям.
     *
     * @param contractorSearch фильтр для поиска (тело запроса в формате JSON)
     * @return ResponseEntity со списком найденных контрагентов
     */
    @PostMapping("/search")
    @Operation(summary = "Поиск контрагента по фильтру")
    @PreAuthorize("hasAnyRole('CONTRACTOR_RUS','CONTRACTOR_SUPERUSER','SUPERUSER')")
    public ResponseEntity<List<ContractorDto>> search(
        @RequestBody ContractorSearch contractorSearch,
        Authentication authentication
    ) {
        log.info("Поиск контрагента по фильтру");
        return ResponseEntity.ok(contractorRepository.filterRusSearch(contractorSearch, authentication));
    }

}
