package com.nhnacademy.shop.common.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductType {
    BOOK("BOOK", "국내도서"),
    MUSIC("MUSIC", "음반"),
    DVD("DVD", "Dvd"),
    FOREIGN("FOREIGN", "외국도서"),
    EBOOK("EBOOK", "전자책");

    private final String status;
    private final String displayName;

    ProductType(String status, String displayName) {
        this.status = status;
        this.displayName = displayName;
    }
    public static ProductType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("ProductType is null");
        }

        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown ProductType: " + value));
    }
}
