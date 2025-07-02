package com.myproject.currencylog.repository;

import com.myproject.currencylog.models.jpa.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByCharCode(String charCode);
    Optional<CountryEntity> findByNumCode(String numCode);
}