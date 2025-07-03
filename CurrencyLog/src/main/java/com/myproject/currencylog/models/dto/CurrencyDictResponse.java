package com.myproject.currencylog.models.dto;

public record CurrencyDictResponse(
        String charCode,
        String numCode,
        String name
) {}