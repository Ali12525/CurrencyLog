package com.myproject.currencylog.controller;

import com.myproject.currencylog.models.jpa.CountryEntity;
import com.myproject.currencylog.repository.CountryRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryRepository countryRepository;

    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Operation(summary = "Получить справочник стран-носителей валюты")
    @GetMapping
    public List<CountryEntity> getAllCountries() {
        return countryRepository.findAll();
    }

    @Operation(summary = "Получить одну страну по её двухбуквенному коду")
    @GetMapping("/{charCode}")
    public CountryEntity getCountryByCharCode(@PathVariable String charCode) {
        return countryRepository.findByCharCode(charCode)
                .orElseThrow(() -> new EntityNotFoundException("Страна с кодом " + charCode + " не найдена"));
    }

    @Operation(summary = "Получить одну страну по её цифровому коду")
    @GetMapping("/num/{numCode}")
    public CountryEntity getByNumCode(@PathVariable String numCode) {
        return countryRepository.findByNumCode(numCode)
                .orElseThrow(() -> new EntityNotFoundException("Страна с numCode=" + numCode + " не найдена"));
    }
}