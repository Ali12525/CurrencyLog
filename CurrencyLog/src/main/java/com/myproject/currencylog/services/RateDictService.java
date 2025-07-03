package com.myproject.currencylog.services;

import com.myproject.currencylog.exceptions.NotFoundException;
import com.myproject.currencylog.models.dto.CurrencyDictResponse;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.repository.RateDictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RateDictService {

    private final RateDictRepository rateDictRepository;

    @Transactional(readOnly = true)
    public List<CurrencyDictResponse> getAll() {
        return rateDictRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CurrencyDictResponse getByCharCode(String charCode) {
        return rateDictRepository.findByCharCode(charCode)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException("Валюта с кодом " + charCode + " не найдена"));
    }

    @Transactional(readOnly = true)
    public CurrencyDictResponse getByNumCode(String numCode) {
        return rateDictRepository.findByNumCode(numCode)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException("Валюта с numCode=" + numCode + " не найдена"));
    }

    private CurrencyDictResponse mapToDto(RateDictEntity entity) {
        return new CurrencyDictResponse(entity.getCharCode(), entity.getNumCode(), entity.getName());
    }
}