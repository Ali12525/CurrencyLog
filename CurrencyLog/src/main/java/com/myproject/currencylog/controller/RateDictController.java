package com.myproject.currencylog.controller;

import com.myproject.currencylog.models.dto.CurrencyDictResponse;
import com.myproject.currencylog.services.RateDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currencies/dict")
@RequiredArgsConstructor
public class RateDictController {
    private final RateDictService rateDictService;

    @Operation(summary = "Получить справочник валюты")
    @GetMapping
    public List<CurrencyDictResponse> getAllCurrencyDict() {
        return rateDictService.getAll();
    }

    @Operation(summary = "Получить одну валюту по трёхбуквенному коду", parameters = {
            @Parameter(name = "charCode", example = "AUD")})
    @GetMapping("/char-code/{charCode}")
    public CurrencyDictResponse getByCharCode(@PathVariable @Pattern(regexp = "[A-Z]{3}", message = "Код должен состоять из 3 заглавных букв") String charCode) {
        return rateDictService.getByCharCode(charCode);
    }

    @Operation(summary = "Получить одну валюту по цифровому коду", parameters = {
            @Parameter(name = "numCode", example = "036")})
    @GetMapping("/num-code/{numCode}")
    public CurrencyDictResponse getByNumCode(@PathVariable @Pattern(regexp = "\\d{3}", message = "Код должен состоять из 3 цифр") String numCode) {
        return rateDictService.getByNumCode(numCode);
    }
}