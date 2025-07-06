package com.myproject.currencylog;

import com.myproject.currencylog.controller.RateController;
import com.myproject.currencylog.exceptions.NotFoundException;
import com.myproject.currencylog.models.dto.RateResponse;
import com.myproject.currencylog.models.dto.RateUpdateRequest;
import com.myproject.currencylog.services.RateCommandService;
import com.myproject.currencylog.services.RateQueryService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class RateControllerTest {
    @Mock
    private RateQueryService queryService;

    @Mock
    private RateCommandService commandService;

    @InjectMocks
    private RateController rateController;

    @Test
    void updateRate_Success() {
        RateUpdateRequest request = new RateUpdateRequest("USD", LocalDate.now(), BigDecimal.valueOf(75.5), 1l);
        RateResponse response = new RateResponse(
                "US", "USD", "840", LocalDate.now(), BigDecimal.valueOf(75.5), 1l, "Доллар США"
        );

        when(commandService.updateRate(any())).thenReturn(response);

        ResponseEntity<RateResponse> result = rateController.updateRate(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void updateRate_NotFound() {
        LocalDate today = LocalDate.now();
        RateUpdateRequest request = new RateUpdateRequest("XXX", today, BigDecimal.valueOf(75.5), 1l);

        when(commandService.updateRate(any())).thenThrow(new NotFoundException("Валюта 'XXX' не найдена"));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> rateController.updateRate(request)
        );

        assertEquals("Валюта 'XXX' не найдена", exception.getMessage());
    }

    @Test
    void getRates_WithFilters_Success() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        RateResponse rateResponse = new RateResponse(
                "US", "USD", "840", today, BigDecimal.valueOf(75.5), 1L, "Доллар США"
        );
        Page<RateResponse> page = new PageImpl<>(List.of(rateResponse));

        when(queryService.findRates(
                eq(Optional.of("US")),
                eq(Optional.of("USD")),
                eq(Optional.of("840")),
                eq(Optional.of(weekAgo)),
                eq(Optional.of(today)),
                any(Pageable.class)
        )).thenReturn(page);

        Page<RateResponse> result = rateController.getRates("US", "USD", "840", weekAgo, today, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals(rateResponse, result.getContent().get(0));
    }

    @Test
    void getRates_EmptyResult() {
        Page<RateResponse> emptyPage = Page.empty();
        when(queryService.findRates(
                any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(emptyPage);

        Page<RateResponse> result = rateController.getRates(
                "XX", "XXX", "999", null, null, Pageable.unpaged()
        );

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getRates_PaginationCheck() {
        Pageable pageable = PageRequest.of(1, 20, Sort.by("rateDate").descending());

        when(queryService.findRates(
                any(), any(), any(), any(), any(), eq(pageable)
        )).thenReturn(Page.empty());

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
}
