package com.myproject.currencylog.repository;

import com.myproject.currencylog.models.jpa.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByCharCode(String charCode);
    Optional<CountryEntity> findByNumCode(String numCode);

    @Query("SELECT c FROM CountryEntity c WHERE c.numCode IN :codes")
    List<CountryEntity> findByNumCodeIn(@Param("codes") List<String> codes);
}