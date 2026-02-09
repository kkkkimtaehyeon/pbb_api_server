package com.nhnacademy.shop.common.enums;

import lombok.Getter;

@Getter
public enum ProductStatus {
    SELLING("SELLING", "판매중"),
    SOLD_OUT("SOLD_OUT", "품절"),
    HIDED("HIDED", "숨김");

    private final String status;
    private final String displayName;

    ProductStatus(String status, String displayName) {
        this.status = status;
        this.displayName = displayName;
    }

}
