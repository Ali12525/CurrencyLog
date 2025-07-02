package com.myproject.currencylog.repository;

import com.myproject.currencylog.models.jpa.RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface RateRepository extends JpaRepository<RateEntity, Long>, JpaSpecificationExecutor<RateEntity> {
    @Query("SELECT r FROM RateEntity r JOIN FETCH r.rateDict WHERE r.rateDate = :rateDate")
    List<RateEntity> findByRateDateWithDict(@Param("rateDate") LocalDateTime rateDate);
}