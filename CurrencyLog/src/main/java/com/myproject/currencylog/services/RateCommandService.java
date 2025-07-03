package com.myproject.currencylog.services;

import com.myproject.currencylog.exceptions.NotFoundException;
import com.myproject.currencylog.mapper.RateMapper;
import com.myproject.currencylog.models.dto.RateResponse;
import com.myproject.currencylog.models.dto.RateUpdateRequest;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.repository.RateDictRepository;
import com.myproject.currencylog.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RateCommandService {

    private final RateRepository rateRepository;
    private final RateDictRepository rateDictRepository;
    private final RateMapper rateMapper;

    @Transactional
    public RateResponse updateRate(RateUpdateRequest request) {
        RateDictEntity rateDict = rateDictRepository.findByCharCode(request.currencyCode())
                .orElseThrow(() -> new NotFoundException("Валюта с кодом '" + request.currencyCode() + "' не найдена"));

        LocalDateTime rateDateTime = request.rateDate().atStartOfDay();

        RateEntity rate = rateRepository.findByRateDictIdAndRateDate(rateDict.getId(), rateDateTime)
                .orElseThrow(() -> new NotFoundException("Курс для валюты " + request.currencyCode() + " на дату " + request.rateDate() + " не найден"));

        rate.setValue(request.value());
        rate.setNominal(request.nominal());
        rate.setUpdated(LocalDateTime.now());

        rateRepository.save(rate);
        return rateMapper.toDto(rate);
    }
}