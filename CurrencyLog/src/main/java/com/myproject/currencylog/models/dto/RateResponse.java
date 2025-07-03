package com.myproject.currencylog.models.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RateResponse(
        String countryCode,
        String currencyCode,
        String numCode,
        LocalDate date,
        BigDecimal value,
        Long nominal,
        String currencyName
) {}