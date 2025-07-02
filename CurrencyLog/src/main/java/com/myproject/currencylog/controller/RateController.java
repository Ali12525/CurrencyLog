package com.myproject.currencylog.controller;

import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.services.RateQueryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/rates")
public class RateController {

    private final RateQueryService queryService;

    public RateController(RateQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Получить курсы с фильтрацией, пагинацией и сортировкой")
    @GetMapping
    public Page<RateEntity> getRates(
            @RequestParam Optional<String> countryCode,
            @RequestParam Optional<String> currencyCode,
            @RequestParam Optional<String> numCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> dateTo,
            Pageable pageable
    ) {
        return queryService.findRates(countryCode, currencyCode, numCode, dateFrom, dateTo, pageable);
    }

}