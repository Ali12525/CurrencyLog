package com.myproject.currencylog.repository;

import com.myproject.currencylog.models.jpa.RateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RateRepository extends JpaRepository<RateEntity, Long>, JpaSpecificationExecutor<RateEntity> {
    @Query("SELECT r FROM RateEntity r JOIN FETCH r.rateDict WHERE r.rateDate = :rateDate")
    List<RateEntity> findByRateDateWithDict(@Param("rateDate") LocalDateTime rateDate);

    @EntityGraph(attributePaths = {"rateDict", "country"})
    Page<RateEntity> findAll(Specification<RateEntity> spec, Pageable pageable);

    @Query("SELECT r FROM RateEntity r WHERE r.rateDictId = :rateDictId AND r.rateDate = :rateDate")
    Optional<RateEntity> findByRateDictIdAndRateDate(
            @Param("rateDictId") Long rateDictId,
            @Param("rateDate") LocalDateTime rateDate
    );
}