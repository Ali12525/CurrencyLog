package com.myproject.currencylog.services;

import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.repository.RateRepository;
import com.myproject.currencylog.spec.RateSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class RateQueryService {

    private final RateRepository rateRepository;

    public RateQueryService(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public Page<RateEntity> findRates(
            Optional<String> countryCode,
            Optional<String> currencyCode,
            Optional<String> numCode,
            Optional<LocalDate> dateFrom,
            Optional<LocalDate> dateTo,
            Pageable pageable
    ) {
        Specification<RateEntity> spec = Specification.where(null);

        if (countryCode.isPresent()) {
            spec = spec.and(RateSpecifications.hasCountryCharCode(countryCode.get()));
        }
        if (currencyCode.isPresent()) {
            spec = spec.and(RateSpecifications.hasCurrencyCharCode(currencyCode.get()));
        }
        if (numCode.isPresent()) {
            spec = spec.and(RateSpecifications.hasRateDictNumCode(numCode.get()));
        }
        if (dateFrom.isPresent() && dateTo.isPresent()) {
            LocalDateTime from = dateFrom.get().atStartOfDay();
            LocalDateTime to   = dateTo.get().atTime(LocalTime.MAX);
            spec = spec.and(RateSpecifications.hasRateDateBetween(from, to));
        }

        return rateRepository.findAll(spec, pageable);
    }
}