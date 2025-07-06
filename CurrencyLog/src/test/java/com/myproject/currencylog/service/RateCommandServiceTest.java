package com.myproject.currencylog.service;

import com.myproject.currencylog.exceptions.NotFoundException;
import com.myproject.currencylog.mapper.RateMapper;
import com.myproject.currencylog.models.dto.RateResponse;
import com.myproject.currencylog.models.dto.RateUpdateRequest;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.repository.RateDictRepository;
import com.myproject.currencylog.repository.RateRepository;
import com.myproject.currencylog.services.RateCommandService;
import com.myproject.currencylog.util.EntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static com.myproject.currencylog.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateCommandServiceTest {
    @Mock
    private RateRepository rateRepository;

    @Mock
    private RateDictRepository rateDictRepository;

    @Mock
    private RateMapper rateMapper;

    @InjectMocks
    private RateCommandService commandService;

    private final RateDictEntity USD_DICT = EntityFactory.createRateDict(1L, USD_CURRENCY_CODE, USD_NUM_CODE, USD_NAME);
    private final RateUpdateRequest VALID_REQUEST = new RateUpdateRequest(USD_CURRENCY_CODE, TODAY, TEST_VALUE_1, 1L);
    private final RateUpdateRequest INVALID_REQUEST = new RateUpdateRequest("XXX", TODAY, TEST_VALUE_1, 1L);
    private final RateResponse RATE_RESPONSE = new RateResponse(US_COUNTRY_CODE, USD_CURRENCY_CODE, USD_NUM_CODE, TODAY, TEST_VALUE_1, 1L, USD_NAME);

    @Test
    void updateRate_Success() {
        RateEntity rateEntity = new RateEntity();

        when(rateDictRepository.findByCharCode(USD_CURRENCY_CODE)).thenReturn(Optional.of(USD_DICT));
        when(rateRepository.findByRateDictIdAndRateDate(any(), any())).thenReturn(Optional.of(rateEntity));
        when(rateMapper.toDto(any())).thenReturn(RATE_RESPONSE);

        RateResponse result = commandService.updateRate(VALID_REQUEST);

        assertEquals(TEST_VALUE_1, rateEntity.getValue());
        assertEquals(1, rateEntity.getNominal());
        assertEquals(RATE_RESPONSE, result);
        assertNotNull(rateEntity.getUpdated());
        verify(rateRepository).save(rateEntity);
    }

    @Test
    void updateRate_CurrencyNotFound() {
        when(rateDictRepository.findByCharCode("XXX")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> commandService.updateRate(INVALID_REQUEST));

        assertEquals("Валюта с кодом 'XXX' не найдена", exception.getMessage());
    }
}