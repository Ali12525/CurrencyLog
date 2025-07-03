package com.myproject.currencylog.models.dto;

public record CountryResponse(
        String charCode,
        String numCode,
        String name
) {}