package com.myproject.currencylog.models.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "countries")
public class CountryEntity extends AbstractBaseDictEntity {
    @OneToMany(mappedBy = "country")
    List<RateEntity> rates;
}