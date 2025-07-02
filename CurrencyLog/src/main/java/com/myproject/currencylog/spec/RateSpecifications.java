package com.myproject.currencylog.spec;

import com.myproject.currencylog.models.jpa.RateEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class RateSpecifications {

    public static Specification<RateEntity> hasRateDateBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) ->
                cb.between(root.get("rateDate"), from, to);
    }

    public static Specification<RateEntity> hasRateDictNumCode(String numCode) {
        return (root, query, cb) ->
                cb.equal(root.join("rateDict").get("numCode"), numCode);
    }

    public static Specification<RateEntity> hasCountryCharCode(String countryCode) {
        return (root, query, cb) ->
                cb.equal(root.join("country").get("charCode"), countryCode);
    }

    public static Specification<RateEntity> hasCurrencyCharCode(String currencyCode) {
        return (root, query, cb) ->
                cb.equal(root.join("rateDict").get("charCode"), currencyCode);
    }
}