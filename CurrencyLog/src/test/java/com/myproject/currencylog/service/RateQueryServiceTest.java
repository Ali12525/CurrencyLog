package com.myproject.currencylog.service;

import com.myproject.currencylog.mapper.RateMapper;
import com.myproject.currencylog.models.dto.RateResponse;
import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.repository.RateRepository;
import com.myproject.currencylog.services.RateQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static com.myproject.currencylog.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RateQueryServiceTest {
    @Mock
    private RateRepository rateRepository;

    @Mock
    private RateMapper rateMapper;

    @InjectMocks
    private RateQueryService queryService;

    private final RateResponse RATE_RESPONSE = new RateResponse(US_COUNTRY_CODE, USD_CURRENCY_CODE, USD_NUM_CODE, TODAY, TEST_VALUE_1, 1L, USD_NAME);

    @Test
    void findRates_WithFilters() {
        RateEntity entity = new RateEntity();
        Page<RateEntity> page = new PageImpl<>(List.of(entity));

        when(rateRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(rateMapper.toDto(entity)).thenReturn(RATE_RESPONSE);

        Page<RateResponse> result = queryService.findRates(
                Optional.of(US_COUNTRY_CODE),
                Optional.of(USD_CURRENCY_CODE),
                Optional.of(USD_NUM_CODE),
                Optional.of(TODAY.minusDays(7)),
                Optional.of(TODAY),
                Pageable.unpaged()
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(RATE_RESPONSE, result.getContent().get(0));
    }
}