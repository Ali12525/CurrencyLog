package com.myproject.currencylog.controller;

import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.repository.RateDictRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currencies/dict")
public class RateDictController {

    private final RateDictRepository rateDictRepository;

    public RateDictController(RateDictRepository rateDictRepository) {
        this.rateDictRepository = rateDictRepository;
    }

    @Operation(summary = "Получить справочник валюты")
    @GetMapping
    public List<RateDictEntity> getAllCurrencyDict() {
        return rateDictRepository.findAll();
    }

    @Operation(summary = "Получить одну валюту по трёхбуквенному коду")
    @GetMapping("/{charCode}")
    public RateDictEntity getByCharCode(@PathVariable String charCode) {
        return rateDictRepository.findByCharCode(charCode)
                .orElseThrow(() -> new EntityNotFoundException("Валюта с кодом " + charCode + " не найдена"));
    }

    @Operation(summary = "Получить одну валюту по цифровому коду")
    @GetMapping("/num/{numCode}")
    public RateDictEntity getByNumCode(@PathVariable String numCode) {
        return rateDictRepository.findByNumCode(numCode)
                .orElseThrow(() -> new EntityNotFoundException("Валюта с numCode=" + numCode + " не найдена"));
    }
}