package com.myproject.currencylog.repository;

import com.myproject.currencylog.models.jpa.RateDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateDictRepository extends JpaRepository<RateDictEntity, Long> {
    Optional<RateDictEntity> findByCharCode(String charCode);
    Optional<RateDictEntity> findByNumCode(String numCode);
}