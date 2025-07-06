package com.myproject.currencylog;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateQueryServiceTest {

    @Mock
    private RateRepository rateRepository;

    @Mock
    private RateMapper rateMapper;

    @InjectMocks
    private RateQueryService queryService;

    @Test
    void findRates_WithFilters() {
        RateEntity entity = new RateEntity();
        RateResponse response = new RateResponse(
                "US", "USD", "840", LocalDate.now(), BigDecimal.valueOf(75.5), 1l, "Доллар США"
        );
        Page<RateEntity> page = new PageImpl<>(Collections.singletonList(entity));

        when(rateRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(rateMapper.toDto(entity)).thenReturn(response);

        Page<RateResponse> result = queryService.findRates(
                Optional.of("US"),
                Optional.of("USD"),
                Optional.of("840"),
                Optional.of(LocalDate.now().minusDays(7)),
                Optional.of(LocalDate.now()),
                Pageable.unpaged()
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));
    }
}