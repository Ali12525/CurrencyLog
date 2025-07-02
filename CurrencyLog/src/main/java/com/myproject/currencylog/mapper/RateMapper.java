package com.myproject.currencylog.mapper;

import com.myproject.currencylog.models.dto.CurrencyRate;
import com.myproject.currencylog.models.jpa.CountryEntity;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.models.jpa.RateEntity;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class RateMapper {
    public RateEntity toEntity(CurrencyRate currencyRate,
                               RateDictEntity dict,
                               CountryEntity country,
                               LocalDateTime rateDate,
                               BigDecimal value,
                               LocalDateTime now) {
        RateEntity rate = new RateEntity();
        rate.setCurrencyId(currencyRate.getId());
        rate.setCountryId(country.getId());
        rate.setRateDictId(dict.getId());
        rate.setRateDate(rateDate);
        rate.setNominal(currencyRate.getNominal());
        rate.setValue(value);
        rate.setCreated(now);
        rate.setUpdated(now);
        return rate;
    }
}