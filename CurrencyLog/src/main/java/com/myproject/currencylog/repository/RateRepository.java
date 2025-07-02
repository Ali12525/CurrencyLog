package com.myproject.currencylog.repository;

import com.myproject.currencylog.models.jpa.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RateRepository extends JpaRepository<RateEntity, Long>, JpaSpecificationExecutor<RateEntity> {
    Optional<RateEntity> findByRateDateAndRateDictNumCode(LocalDateTime rateDate, String numCode);
}