package com.myproject.currencylog.services;

import com.myproject.currencylog.exceptions.NotFoundException;
import com.myproject.currencylog.models.dto.CountryResponse;
import com.myproject.currencylog.models.jpa.CountryEntity;
import com.myproject.currencylog.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public List<CountryResponse> getAll() {
        return countryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CountryResponse getByCharCode(String charCode) {
        return countryRepository.findByCharCode(charCode)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Страна с кодом '" + charCode + "' не найдена"));
    }

    @Transactional(readOnly = true)
    public CountryResponse getByNumCode(String numCode) {
        return countryRepository.findByNumCode(numCode)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Страна с цифровым кодом '" + numCode + "' не найдена"));
    }

    private CountryResponse convertToDto(CountryEntity entity) {
        return new CountryResponse(entity.getCharCode(), entity.getNumCode(), entity.getName());
    }
}
