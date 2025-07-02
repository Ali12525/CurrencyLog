package com.myproject.currencylog.repository;

import com.myproject.currencylog.models.jpa.RateDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RateDictRepository extends JpaRepository<RateDictEntity, Long> {
    Optional<RateDictEntity> findByCharCode(String charCode);
    Optional<RateDictEntity> findByNumCode(String numCode);

    @Query("SELECT d FROM RateDictEntity d WHERE d.numCode IN :codes")
    List<RateDictEntity> findByNumCodeIn(@Param("codes") List<String> codes);
}