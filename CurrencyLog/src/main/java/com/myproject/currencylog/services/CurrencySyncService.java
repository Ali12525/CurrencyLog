package com.myproject.currencylog.services;

import com.myproject.currencylog.integration.CbrClient;
import com.myproject.currencylog.mapper.RateMapper;
import com.myproject.currencylog.models.dto.CbrDailyResponse;
import com.myproject.currencylog.models.jpa.CountryEntity;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.repository.CountryRepository;
import com.myproject.currencylog.repository.RateDictRepository;
import com.myproject.currencylog.repository.RateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

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
        this.rateRepository    = rateRepository;
        this.rateDictRepository = rateDictRepository;
        this.countryRepository = countryRepository;
    }

    @Scheduled(fixedRateString = "${currency.sync.rate:3600000}")
    public void syncHourly() {
        try {
            CbrDailyResponse resp = cbrClient.fetchDaily();

            LocalDateTime rateDate = parseDate(resp.getDate());
            LocalDateTime now = LocalDateTime.now();

            Map<String, RateDictEntity> dictByNum = rateDictRepository.findAll().stream().collect(toMap(RateDictEntity::getNumCode, identity()));
            Map<String, CountryEntity> countryByNum = countryRepository.findAll().stream().collect(toMap(CountryEntity::getNumCode, identity()));

            var rates = rateMapper.map(resp.getValute(), dictByNum, countryByNum, rateDate, now);
            if (!rates.isEmpty()) {
                rateRepository.saveAll(rates);
            }
        } catch (Exception e) {
            log.error("Ошибка синхронизации курсов валют", e);
        }
    }

    private LocalDateTime parseDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
        return date.atStartOfDay();
    }
}