package com.myproject.currencylog.models.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rates")
public class RateEntity extends AbstractBaseFixationDateTimeEntity {
    @Column(name = "currency_id")
    private String currencyId;

    @Column(name = "country_id")
    private Long countryId;

    @ManyToOne
    @JoinColumn(name = "country_id", insertable = false, updatable = false)
    private CountryEntity country;

    @Column(name = "rate_dict_id")
    private Long rateDictId;

    @ManyToOne
    @JoinColumn(name = "rate_dict_id", insertable = false, updatable = false)
    private RateDictEntity rateDict;

    @Column(name = "nominal")
    private Long nominal;

    @Column(name = "value", precision = 15, scale = 6)
    private BigDecimal value;

    @Column(name = "rate_date")
    private LocalDateTime rateDate;

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    public Long getRateDictId() {
        return rateDictId;
    }

    public void setRateDictId(Long rateDictId) {
        this.rateDictId = rateDictId;
    }

    public RateDictEntity getRateDict() {
        return rateDict;
    }

    public void setRateDict(RateDictEntity rateDict) {
        this.rateDict = rateDict;
    }

    public Long getNominal() {
        return nominal;
    }

    public void setNominal(Long nominal) {
        this.nominal = nominal;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDateTime getRateDate() {
        return rateDate;
    }

    public void setRateDate(LocalDateTime rateDate) {
        this.rateDate = rateDate;
    }
}