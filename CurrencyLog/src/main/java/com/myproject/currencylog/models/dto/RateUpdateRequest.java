package com.myproject.currencylog.models.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RateUpdateRequest(
        @NotBlank(message = "Код валюты обязателен для заполнения")
        @Pattern(regexp = "[A-Z]{3}", message = "Код валюты должен состоять из 3 заглавных букв")
        String currencyCode,

        @NotNull(message = "Дата курса обязательна для заполнения")
        LocalDate rateDate,

        @NotNull(message = "Значение курса обязательно для заполнения")
        @DecimalMin(value = "0.000001", message = "Значение курса должно быть больше 0")
        BigDecimal value,

        @NotNull(message = "Номинал обязателен для заполнения")
        @Min(value = 1, message = "Номинал должен быть больше 0")
        Long nominal
) {}