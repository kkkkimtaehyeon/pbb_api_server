package com.nhnacademy.shop.admin.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
public final class BookProductRegistrationRequest extends ProductRegistrationRequest {
    private String title;
    private BigDecimal priceStandard;
    private String isbn13;
    private LocalDate publishDate;
    private String summary;
    private List<Long> authorIds;
    private Long publisherId;
}