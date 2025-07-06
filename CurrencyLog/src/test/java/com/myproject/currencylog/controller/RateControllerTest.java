package com.myproject.currencylog.controller;

import com.myproject.currencylog.exceptions.NotFoundException;
import com.myproject.currencylog.models.dto.RateResponse;
import com.myproject.currencylog.models.dto.RateUpdateRequest;
import com.myproject.currencylog.services.RateCommandService;
import com.myproject.currencylog.services.RateQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.myproject.currencylog.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RateControllerTest {
    @Mock
    private RateQueryService queryService;

    @Mock
    private RateCommandService commandService;

    @InjectMocks
    private RateController rateController;

    private final RateUpdateRequest UPDATE_REQUEST = new RateUpdateRequest(USD_CURRENCY_CODE, TODAY, TEST_VALUE_1, 1L);
    private final RateResponse RATE_RESPONSE = new RateResponse(US_COUNTRY_CODE, USD_CURRENCY_CODE, USD_NUM_CODE, TODAY, TEST_VALUE_1, 1L, USD_NAME);
    private final RateResponse AUD_RESPONSE = new RateResponse(AU_COUNTRY_CODE, AUD_CURRENCY_CODE, AUD_NUM_CODE, TODAY, TEST_VALUE_2, 1L, AUD_NAME);

    @Test
    void updateRate_Success() {
        when(commandService.updateRate(any())).thenReturn(RATE_RESPONSE);

        ResponseEntity<RateResponse> result = rateController.updateRate(UPDATE_REQUEST);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(RATE_RESPONSE, result.getBody());
    }

    @Test
    void updateRate_NotFound() {
        when(commandService.updateRate(any())).thenThrow(new NotFoundException("Валюта 'XXX' не найдена"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> rateController.updateRate(new RateUpdateRequest("XXX", TODAY, TEST_VALUE_1, 1L))
        );

        assertEquals("Валюта 'XXX' не найдена", exception.getMessage());
    }

    @Test
    void getRates_WithFilters_Success() {
        Page<RateResponse> page = new PageImpl<>(List.of(RATE_RESPONSE));

        when(queryService.findRates(
                eq(Optional.of(US_COUNTRY_CODE)),
                eq(Optional.of(USD_CURRENCY_CODE)),
                eq(Optional.of(USD_NUM_CODE)),
                eq(Optional.of(TODAY.minusDays(7))),
                eq(Optional.of(TODAY)),
                any(Pageable.class)
        )).thenReturn(page);

        Page<RateResponse> result = rateController.getRates(
                US_COUNTRY_CODE, USD_CURRENCY_CODE, USD_NUM_CODE,
                TODAY.minusDays(7), TODAY, Pageable.unpaged()
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(RATE_RESPONSE, result.getContent().get(0));
    }

    @Test
    void getRates_EmptyResult() {
        when(queryService.findRates(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        Page<RateResponse> result = rateController.getRates(
                "XX", "XXX", "999", null, null, Pageable.unpaged()
        );

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getRates_PaginationCheck() {
        Pageable pageable = PageRequest.of(1, 20, Sort.by("rateDate").descending());

        when(queryService.findRates(any(), any(), any(), any(), any(), eq(pageable))).thenReturn(Page.empty());

        rateController.getRates(null, null, null, null, null, pageable);

        verify(queryService).findRates(
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(pageable)
        );
    }

    @Test
    void getRates_PartialFilters() {
        ArgumentCaptor<Optional<LocalDate>> dateFromCaptor = ArgumentCaptor.forClass(Optional.class);
        ArgumentCaptor<Optional<LocalDate>> dateToCaptor = ArgumentCaptor.forClass(Optional.class);

        when(queryService.findRates(
                any(),
                any(),
                any(),
                dateFromCaptor.capture(),
                dateToCaptor.capture(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        rateController.getRates(null, null, null, TODAY, null, Pageable.unpaged());
        rateController.getRates(null, null, null, null, TODAY, Pageable.unpaged());
        rateController.getRates("AU", null, null, null, null, Pageable.unpaged());

        List<Optional<LocalDate>> dateFromValues = dateFromCaptor.getAllValues();
        assertEquals(Optional.of(TODAY), dateFromValues.get(0));
        assertEquals(Optional.empty(), dateFromValues.get(1));
        assertEquals(Optional.empty(), dateFromValues.get(2));

        List<Optional<LocalDate>> dateToValues = dateToCaptor.getAllValues();
        assertEquals(Optional.empty(), dateToValues.get(0));
        assertEquals(Optional.of(TODAY), dateToValues.get(1));
        assertEquals(Optional.empty(), dateToValues.get(2));
    }

    @Test
    void getRates_MultipleCurrencies() {
        Page<RateResponse> page = new PageImpl<>(List.of(RATE_RESPONSE, AUD_RESPONSE));

        when(queryService.findRates(
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty()),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(page);

        Page<RateResponse> result = rateController.getRates(
                null, null, null, null, null, Pageable.unpaged()
        );

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().contains(RATE_RESPONSE));
        assertTrue(result.getContent().contains(AUD_RESPONSE));
    }
}