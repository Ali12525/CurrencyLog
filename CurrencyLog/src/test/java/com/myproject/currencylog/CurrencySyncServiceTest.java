package com.myproject.currencylog;

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
import com.myproject.currencylog.services.CurrencySyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

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

    @Test
    void syncCurrency_NewRate() {
        CbrDailyResponse response = new CbrDailyResponse();
        response.setDate("05.07.2025");
        response.setCurrencyRate(List.of(
                new CurrencyRate("1", "036", "AUD", 1l, "Австралийский доллар", "51.2345")
        ));

        RateDictEntity rateDict = new RateDictEntity();
        rateDict.setId(1L);
        rateDict.setCharCode("AUD");
        rateDict.setNumCode("036");
        rateDict.setName("Австралийский доллар");

        CountryEntity country = new CountryEntity();
        country.setId(1L);
        country.setCharCode("AU");
        country.setNumCode("036");
        country.setName("Австралия");

        RateEntity newRate = new RateEntity();

        when(cbrClient.fetchDaily()).thenReturn(response);
        when(rateDictRepository.findByNumCodeIn(any())).thenReturn(List.of(rateDict));
        when(countryRepository.findByNumCodeIn(any())).thenReturn(List.of(country));
        when(rateRepository.findByRateDateWithDict(any())).thenReturn(List.of());
        when(rateMapper.toEntity(any(), any(), any(), any(), any(), any())).thenReturn(newRate);

        syncService.syncCurrency();

        ArgumentCaptor<List<RateEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(rateRepository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    @Test
    void syncCurrency_UpdateExistingRate() {
        CbrDailyResponse response = new CbrDailyResponse();
        response.setDate("05.07.2025");
        response.setCurrencyRate(List.of(
                new CurrencyRate("R01010", "036", "AUD", 1l, "Австралийский доллар", "52.1234")
        ));

        RateDictEntity rateDict = new RateDictEntity();
        rateDict.setId(1L);
        rateDict.setCharCode("AUD");
        rateDict.setNumCode("036");
        rateDict.setName("Австралийский доллар");

        CountryEntity country = new CountryEntity();
        country.setId(1L);
        country.setCharCode("AU");
        country.setNumCode("036");
        country.setName("Австралия");

        RateEntity existingRate = new RateEntity();
        existingRate.setValue(BigDecimal.valueOf(50.0));
        existingRate.setRateDict(rateDict);
        existingRate.setCountry(country);

        when(cbrClient.fetchDaily()).thenReturn(response);
        when(rateDictRepository.findByNumCodeIn(any())).thenReturn(List.of(rateDict));
        when(countryRepository.findByNumCodeIn(any())).thenReturn(List.of(country));
        when(rateRepository.findByRateDateWithDict(any())).thenReturn(List.of(existingRate));

        syncService.syncCurrency();

        assertEquals(BigDecimal.valueOf(52.1234), existingRate.getValue());
        assertEquals(1, existingRate.getNominal());
        assertNotNull(existingRate.getUpdated());
        verify(rateRepository).saveAll(any());
    }
}