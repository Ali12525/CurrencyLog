package com.myproject.currencylog.mapper;

import com.myproject.currencylog.models.dto.Valute;
import com.myproject.currencylog.models.jpa.CountryEntity;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.repository.RateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RateMapper {

    private final RateRepository rateRepository;

    public RateMapper(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public List<RateEntity> map(
            List<Valute> valutes,
            Map<String, RateDictEntity> dictByNum,
            Map<String, CountryEntity> countryByNum,
            LocalDateTime rateDate,
            LocalDateTime now
    ) {
        return valutes.stream().map(v -> toEntity(v, dictByNum, countryByNum, rateDate, now)).collect(Collectors.toList());
    }

    private RateEntity toEntity(
            Valute valute,
            Map<String, RateDictEntity> dictByNum,
            Map<String, CountryEntity> countryByNum,
            LocalDateTime rateDate,
            LocalDateTime now
    ) {
        String numCode = valute.getNumCode();
        RateDictEntity dict = dictByNum.get(numCode);
        CountryEntity country = countryByNum.get(numCode);
        if (dict == null || country == null) {
            throw new EntityNotFoundException("Не найден справочник или страна для кода " + numCode);
        }

        BigDecimal value = new BigDecimal(valute.getValue().replace(',', '.'));
        Optional<RateEntity> existing = rateRepository.findByRateDateAndRateDictNumCode(rateDate, numCode);

        if (existing.isPresent()) {
            RateEntity rate = existing.get();
            rate.setValue(value);
            rate.setNominal(valute.getNominal());
            rate.setUpdated(now);
            return rate;
        } else {
            RateEntity rate = new RateEntity();
            rate.setCurrencyId(valute.getId());
            rate.setCountryId(country.getId());
            rate.setRateDictId(dict.getId());
            rate.setRateDate(rateDate);
            rate.setNominal(valute.getNominal());
            rate.setValue(value);
            rate.setCreated(now);
            rate.setUpdated(now);
            return rate;
        }
    }
}