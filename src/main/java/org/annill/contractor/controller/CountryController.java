package org.annill.contractor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.annill.contractor.dto.CountryDto;
import org.annill.contractor.service.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер стран
 *
 * @author anailina
 */

@RestController
@Slf4j
@RequestMapping("/country")
@RequiredArgsConstructor
@Tag(name = "Country API", description = "Управление странами")
public class CountryController implements CountryApi {

    private final CountryService service;

    @GetMapping("/all")
    @Operation(description = "Поиск всех стран")
    public ResponseEntity<List<CountryDto>> findAll() {
        log.info("Поиск стран");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(description = "Поиск страны по id")
    public ResponseEntity<CountryDto> getById(@PathVariable String id) {
        log.info("Поиск страны по id");
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/save")
    @Operation(description = "Сохранение страны")
    public ResponseEntity<Void> save(@RequestBody CountryDto country) {
        log.info("Сохранение страны");
        service.save(country);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(description = "Удаление страны по id")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Удаление страны по id");
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
