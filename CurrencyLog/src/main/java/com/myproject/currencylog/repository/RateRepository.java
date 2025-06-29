package com.myproject.currencylog.repository;

import com.myproject.currencylog.models.jpa.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface RateRepository extends JpaRepository<RateEntity, Long> {
    Optional<RateEntity> findByRateDateAndRateDictNumCode(LocalDateTime rateDate, String numCode);
}