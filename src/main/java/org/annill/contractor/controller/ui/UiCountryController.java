package org.annill.contractor.controller.ui;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.annill.contractor.controller.CountryApi;
import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.repository.CountryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления справочником стран. Позволяет получать список стран, добавлять новые и удалять
 * существующие. Для модифицирующих операций требуются права администратора.
 *
 * @author ailina
 */
@RestController
@Slf4j
@RequestMapping("ui/country")
@RequiredArgsConstructor
public class UiCountryController implements CountryApi {

    private final CountryRepository countryRepository;

    /**
     * Возвращает полный список стран.
     *
     * @return ResponseEntity со списком CountryDto
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER','CONTRACTOR_RUS','CONTRACTOR_SUPERUSER','SUPERUSER')")
    @Operation(description = "Поиск всех стран")
    public ResponseEntity<List<CountryDto>> findAll() {
        log.info("Поиск стран");
        return ResponseEntity.ok(countryRepository.findAll());
    }

    /**
     * Находит страну по её идентификатору.
     *
     * @param id UUID страны (путь URL)
     * @return ResponseEntity с данными страны
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','CONTRACTOR_RUS','CONTRACTOR_SUPERUSER','SUPERUSER')")
    @Operation(description = "Поиск страны по id")
    public ResponseEntity<CountryDto> getById(@PathVariable String id) {
        log.info("Поиск страны по id");
        return ResponseEntity.ok(countryRepository.findById(id));
    }

    /**
     * Сохраняет новую или изменяет существующую страну.
     *
     * @param country DTO страны (тело запроса в формате JSON)
     * @return ResponseEntity без тела (Void)
     */
    @PutMapping("/save")
    @Operation(description = "Сохранение страны")
    @PreAuthorize("hasAnyRole('CONTRACTOR_SUPERUSER','SUPERUSER')")
    public ResponseEntity<Void> save(@RequestBody CountryDto country) {
        log.info("Сохранение страны");
        countryRepository.saveOrUpdate(country);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаляет страну по идентификатору.
     *
     * @param id UUID страны (путь URL)
     * @return ResponseEntity без тела (Void)
     */
    @DeleteMapping("/delete/{id}")
    @Operation(description = "Удаление страны по id")
    @PreAuthorize("hasAnyRole('CONTRACTOR_SUPERUSER','SUPERUSER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Удаление страны по id");
        countryRepository.logicalDelete(id);
        return ResponseEntity.ok().build();
    }

}
