package com.nhnacademy.shop.book.v2.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class BookDetailResponse {
    Long id;
    String imageUrl;
    String title;
    String summary;
    String authors;
    String publisher;
    String publishDate;
    BigDecimal priceSales;
    BigDecimal priceStandard;
}
