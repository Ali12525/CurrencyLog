package com.myproject.currencylog.controller;

import com.myproject.currencylog.models.dto.RateResponse;
import com.myproject.currencylog.models.dto.RateUpdateRequest;
import com.myproject.currencylog.services.RateCommandService;
import com.myproject.currencylog.services.RateQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/rates")
public class RateController {

    private final RateQueryService queryService;
    private final RateCommandService commandService;

    public RateController(RateQueryService queryService, RateCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @Operation(summary = "Получить курсы с фильтрацией, пагинацией и сортировкой")
    @GetMapping
    public Page<RateResponse> getRates(
            @RequestParam(required = false)
            @Pattern(regexp = "[A-Z]{2}", message = "Код страны должен состоять из 2 заглавных букв")
            String countryCode,

            @RequestParam(required = false)
            @Pattern(regexp = "[A-Z]{3}", message = "Код валюты должен состоять из 3 заглавных букв")
            String currencyCode,

            @RequestParam(required = false)
            @Pattern(regexp = "\\d{3}", message = "Цифровой код должен состоять из 3 цифр")
            String numCode,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Дата в формате YYYY-MM-DD", example = "2025-01-01")
            LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Дата в формате YYYY-MM-DD", example = "2025-08-01")
            LocalDate dateTo,

            @PageableDefault(sort = "rateDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return queryService.findRates(
                Optional.ofNullable(countryCode),
                Optional.ofNullable(currencyCode),
                Optional.ofNullable(numCode),
                Optional.ofNullable(dateFrom),
                Optional.ofNullable(dateTo),
                pageable
        );
    }

    @Operation(summary = "Обновить курс валюты по дате и коду")
    @PutMapping
    public ResponseEntity<RateResponse> updateRate(
            @Valid @RequestBody RateUpdateRequest updateRequest
    ) {
        RateResponse updatedRate = commandService.updateRate(updateRequest);
        return ResponseEntity.ok(updatedRate);
    }
}