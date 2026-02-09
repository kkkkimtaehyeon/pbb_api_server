package com.nhnacademy.shop.common.enums;

import lombok.Getter;

@Getter
public class ProductStatusResponse {
    private final String status;
    private final String displayName;

    public ProductStatusResponse(ProductStatus status) {
        this.status = status.getStatus();
        this.displayName = status.getDisplayName();
    }
}