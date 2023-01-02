package com.flab.fbayshop.product.model;

import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum ProductStatus {

    SALE("SALE", "판매 중"),
    SOLD_OUT("SOUT", "품절"),
    END("END", "판매 종료"),
    UNKNOWN("", "알수없음"),
    ;

    private final String code;
    private final String description;

    ProductStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ProductStatus findByCode(String code) {
        return Stream.of(values())
            .filter(productStatus -> productStatus.code.equals(code))
            .findAny().orElse(UNKNOWN);
    }
}
