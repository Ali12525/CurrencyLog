package com.myproject.currencylog;

import com.myproject.currencylog.exceptions.NotFoundException;
import com.myproject.currencylog.mapper.RateMapper;
import com.myproject.currencylog.models.dto.RateResponse;
import com.myproject.currencylog.models.dto.RateUpdateRequest;
import com.myproject.currencylog.models.jpa.RateDictEntity;
import com.myproject.currencylog.models.jpa.RateEntity;
import com.myproject.currencylog.repository.RateDictRepository;
import com.myproject.currencylog.repository.RateRepository;
import com.myproject.currencylog.services.RateCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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

    @Test
    void updateRate_Success() {
        RateUpdateRequest request = new RateUpdateRequest(
                "USD", LocalDate.now(), BigDecimal.valueOf(75.5), 1l
        );
        RateDictEntity rateDict = new RateDictEntity();
        rateDict.setId(1L);
        rateDict.setCharCode("USD");
        rateDict.setNumCode("840");
        rateDict.setName("Доллар США");

        RateEntity rateEntity = new RateEntity();
        RateResponse response = new RateResponse(
                "US", "USD", "840", LocalDate.now(), BigDecimal.valueOf(75.5), 1l, "Доллар США"
        );

        when(rateDictRepository.findByCharCode("USD")).thenReturn(Optional.of(rateDict));
        when(rateRepository.findByRateDictIdAndRateDate(1L, LocalDate.now().atStartOfDay())).thenReturn(Optional.of(rateEntity));
        when(rateMapper.toDto(any())).thenReturn(response);

        RateResponse result = commandService.updateRate(request);

        assertEquals(BigDecimal.valueOf(75.5), rateEntity.getValue());
        assertEquals(1, rateEntity.getNominal());
        assertNotNull(rateEntity.getUpdated());
        verify(rateRepository).save(rateEntity);
        assertEquals(response, result);
    }

    @Test
    void updateRate_CurrencyNotFound() {
        RateUpdateRequest request = new RateUpdateRequest("XXX", LocalDate.now(), BigDecimal.valueOf(75.5), 1l);

        when(rateDictRepository.findByCharCode("XXX")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commandService.updateRate(request));
    }
}