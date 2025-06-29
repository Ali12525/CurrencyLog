package com.myproject.currencylog.models.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "rate_dict")
public class RateDictEntity extends AbstractBaseDictEntity {
    @OneToMany(mappedBy = "rateDict")
    List<RateEntity> rates;
}