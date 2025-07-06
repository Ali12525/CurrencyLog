package com.myproject.currencylog.service;


import com.myproject.currencylog.integration.CbrClient;
import com.myproject.currencylog.mapper.RateMapper;
import com.myproject.currencylog.models.dto.CbrDailyResponse;
import com.myproject.currencylog.models.jpa.CountryEntity;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.repository.CountryRepository;
import com.myproject.currencylog.repository.RateDictRepository;
import com.myproject.currencylog.repository.RateRepository;
import com.myproject.currencylog.services.CurrencySyncService;
import com.myproject.currencylog.util.EntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.myproject.currencylog.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencySyncServiceTest {
    @Mock
    private CbrClient cbrClient;

    @Mock
    private RateMapper rateMapper;

    @Mock
    private RateRepository rateRepository;

    @Mock
    private RateDictRepository rateDictRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CurrencySyncService syncService;

    private final RateDictEntity AUD_DICT = EntityFactory.createRateDict(1L, AUD_CURRENCY_CODE, AUD_NUM_CODE, AUD_NAME);
    private final CountryEntity AU_COUNTRY = EntityFactory.createCountry(1L, AU_COUNTRY_CODE, AUD_NUM_CODE, "Австралия");

    @Test
    void syncCurrency_NewRate() {
        CbrDailyResponse response = EntityFactory.createCbrResponse(
                "05.07.2025",
                EntityFactory.createCurrencyRate("1", AUD_NUM_CODE, AUD_CURRENCY_CODE, 1, AUD_NAME, "51.2345")
        );

        when(cbrClient.fetchDaily()).thenReturn(response);
        when(rateDictRepository.findByNumCodeIn(any())).thenReturn(List.of(AUD_DICT));
        when(countryRepository.findByNumCodeIn(any())).thenReturn(List.of(AU_COUNTRY));
        when(rateRepository.findByRateDateWithDict(any())).thenReturn(List.of());
        when(rateMapper.toEntity(any(), any(), any(), any(), any(), any())).thenReturn(EntityFactory.createRate(AUD_DICT, AU_COUNTRY, null));

        syncService.syncCurrency();

        ArgumentCaptor<List<RateEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(rateRepository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    @Test
    void syncCurrency_UpdateExistingRate() {
        CbrDailyResponse response = EntityFactory.createCbrResponse(
                "05.07.2025", EntityFactory.createCurrencyRate("R01010", AUD_NUM_CODE, AUD_CURRENCY_CODE, 1, AUD_NAME, "52.1234")
        );

        RateEntity existingRate = EntityFactory.createRate(AUD_DICT, AU_COUNTRY, BigDecimal.valueOf(50.0));

        when(cbrClient.fetchDaily()).thenReturn(response);
        when(rateDictRepository.findByNumCodeIn(any())).thenReturn(List.of(AUD_DICT));
        when(countryRepository.findByNumCodeIn(any())).thenReturn(List.of(AU_COUNTRY));
        when(rateRepository.findByRateDateWithDict(any())).thenReturn(List.of(existingRate));

        syncService.syncCurrency();

        assertEquals(TEST_VALUE_2, existingRate.getValue());
        assertEquals(1, existingRate.getNominal());
        assertNotNull(existingRate.getUpdated());
        verify(rateRepository).saveAll(any());
    }
}