package com.flab.fbayshop.product.model;

import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum ProductType {

    AUCTION("A", "경매"),
    BUY_NOW("B", "즉시 구매"),
    UNKNOWN("", "알수없음"),
    ;

    private final String code;
    private final String description;

    ProductType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ProductType findByCode(String code) {
        return Stream.of(values())
            .filter(productType -> productType.code.equals(code))
            .findAny().orElse(UNKNOWN);
    }

}
