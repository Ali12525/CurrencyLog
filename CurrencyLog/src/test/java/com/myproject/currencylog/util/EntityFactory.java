package com.myproject.currencylog.util;

import com.myproject.currencylog.models.dto.CbrDailyResponse;
import com.myproject.currencylog.models.dto.CurrencyRate;
import com.myproject.currencylog.models.jpa.CountryEntity;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.models.jpa.RateEntity;

import java.math.BigDecimal;
import java.util.List;

public class EntityFactory {
    public static RateDictEntity createRateDict(Long id, String charCode, String numCode, String name) {
        RateDictEntity entity = new RateDictEntity();
        entity.setId(id);
        entity.setCharCode(charCode);
        entity.setNumCode(numCode);
        entity.setName(name);
        return entity;
    }

    public static CountryEntity createCountry(Long id, String charCode, String numCode, String name) {
        CountryEntity entity = new CountryEntity();
        entity.setId(id);
        entity.setCharCode(charCode);
        entity.setNumCode(numCode);
        entity.setName(name);
        return entity;
    }

    public static RateEntity createRate(RateDictEntity dict, CountryEntity country, BigDecimal value) {
        RateEntity entity = new RateEntity();
        entity.setRateDict(dict);
        entity.setCountry(country);
        entity.setValue(value);
        return entity;
    }

    public static CbrDailyResponse createCbrResponse(String date, CurrencyRate rates) {
        CbrDailyResponse response = new CbrDailyResponse();
        response.setDate(date);
        response.setCurrencyRate(List.of(rates));
        return response;
    }

    public static CurrencyRate createCurrencyRate(String id, String numCode, String charCode, long nominal, String name, String value) {
        return new CurrencyRate(id, numCode, charCode, nominal, name, value);
    }
}