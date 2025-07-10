package org.annill.contractor.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.annill.contractor.entity.Country;
import org.annill.contractor.repository.CountryRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер стран
 */

@RestController
@Slf4j
@RequestMapping("/country")
public class CountryController {

    private final CountryRepository repository;

    public CountryController(CountryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/all")
    public List<Country> findAll() {
        log.info("Поиск стран");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Country getById(@PathVariable String id) {
        log.info("Поиск страны по id");
        return repository.findById(id);
    }

    @PutMapping("/save")
    public void save(@RequestBody Country country) {
        log.info("Сохранение страны");
        repository.saveOrUpdate(country);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        log.info("Удаление страны по id");
        repository.logicalDelete(id);
    }

}
