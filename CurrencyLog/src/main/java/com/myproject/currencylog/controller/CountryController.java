package com.myproject.currencylog.controller;

import com.myproject.currencylog.models.dto.CountryResponse;
import com.myproject.currencylog.services.CountryService;
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
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @Operation(summary = "Получить справочник стран-носителей валюты")
    @GetMapping
    public List<CountryResponse> getAllCountries() {
        return countryService.getAll();
    }

    @Operation(summary = "Получить одну страну по её двухбуквенному коду", parameters = {
            @Parameter(name = "charCode", example = "AU")})
    @GetMapping("/char-code/{charCode}")
    public CountryResponse getByCharCode(@PathVariable @Pattern(regexp = "[A-Z]{2}", message = "Код должен состоять из 2 заглавных букв") String charCode) {
        return countryService.getByCharCode(charCode);
    }

    @Operation(summary = "Получить одну страну по её цифровому коду", parameters = {
            @Parameter(name = "numCode", example = "036")})
    @GetMapping("/num-code/{numCode}")
    public CountryResponse getByNumCode(@PathVariable @Pattern(regexp = "\\d{3}", message = "Код должен состоять из 3 цифр") String numCode) {
        return countryService.getByNumCode(numCode);
    }
}