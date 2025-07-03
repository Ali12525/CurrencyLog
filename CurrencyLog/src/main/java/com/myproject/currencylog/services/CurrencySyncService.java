package com.myproject.currencylog.services;

import com.myproject.currencylog.integration.CbrClient;
import com.myproject.currencylog.mapper.RateMapper;
import com.myproject.currencylog.models.dto.CbrDailyResponse;
import com.myproject.currencylog.models.dto.CurrencyRate;
import com.myproject.currencylog.models.jpa.CountryEntity;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.repository.CountryRepository;
import com.myproject.currencylog.repository.RateDictRepository;
import com.myproject.currencylog.repository.RateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CurrencySyncService {
    private final CbrClient cbrClient;
    private final RateMapper rateMapper;
    private final RateRepository rateRepository;
    private final RateDictRepository rateDictRepository;
    private final CountryRepository countryRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Logger log = LoggerFactory.getLogger(CurrencySyncService.class);

    public CurrencySyncService(
            CbrClient cbrClient,
            RateMapper rateMapper,
            RateRepository rateRepository,
            RateDictRepository rateDictRepository,
            CountryRepository countryRepository) {
        this.cbrClient = cbrClient;
        this.rateMapper = rateMapper;
        this.rateRepository = rateRepository;
        this.rateDictRepository = rateDictRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional
    public void syncCurrency() {
        CbrDailyResponse resp = cbrClient.fetchDaily();
        LocalDateTime rateDate = parseDate(resp.getDate());
        LocalDateTime now = LocalDateTime.now();

        List<String> numCodes = resp.getCurrencyRate().stream().map(CurrencyRate::getNumCode).distinct().toList();

        Map<String, RateDictEntity> dictByNum = rateDictRepository.findByNumCodeIn(numCodes).stream()
                .collect(Collectors.toMap(RateDictEntity::getNumCode, Function.identity()));

        Map<String, CountryEntity> countryByNum = countryRepository.findByNumCodeIn(numCodes).stream()
                .collect(Collectors.toMap(CountryEntity::getNumCode, Function.identity()));

        Map<String, RateEntity> existingRates = rateRepository.findByRateDateWithDict(rateDate).stream()
                .collect(Collectors.toMap(
                        rate -> rate.getRateDict().getNumCode(),
                        Function.identity()
                ));

        List<RateEntity> ratesToSave = new ArrayList<>();

        for (CurrencyRate currencyRate : resp.getCurrencyRate()) {
            String numCode = currencyRate.getNumCode();

            RateDictEntity dict = dictByNum.get(numCode);
            CountryEntity country = countryByNum.get(numCode);

            if (dict == null || country == null) {
                log.warn("Не найден справочник или страна для кода: {}", numCode);
                continue;
            }

            if (existingRates.containsKey(numCode)) {
                RateEntity existing = existingRates.get(numCode);
                updateExistingRate(existing, currencyRate, now);
                ratesToSave.add(existing);
            } else {
                ratesToSave.add(createNewRate(currencyRate, dict, country, rateDate, now));
            }
        }

        if (!ratesToSave.isEmpty()) {
            rateRepository.saveAll(ratesToSave);
        }
    }

    private void updateExistingRate(RateEntity rate, CurrencyRate currencyRate, LocalDateTime now) {
        BigDecimal value = new BigDecimal(currencyRate.getValue().replace(',', '.'));
        rate.setValue(value);
        rate.setNominal(currencyRate.getNominal());
        rate.setUpdated(now);
    }

    private RateEntity createNewRate(CurrencyRate currencyRate,
                                     RateDictEntity dict,
                                     CountryEntity country,
                                     LocalDateTime rateDate,
                                     LocalDateTime now) {
        BigDecimal value = new BigDecimal(currencyRate.getValue().replace(',', '.'));
        return rateMapper.toEntity(currencyRate, dict, country, rateDate, value, now);
    }

    private LocalDateTime parseDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
        return date.atStartOfDay();
    }
}