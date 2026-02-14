package com.nhnacademy.shop.book.v2.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookSimpleResponse {
    Long id;
    String imageUrl;
    String title;
    String authors;
    String publisher;
    String publishDate;
    BigDecimal priceStandard;
    BigDecimal priceSales;
    String categoryName;

    @Builder
    public BookSimpleResponse(Long id, String imageUrl, String title, String authors, String publisher,
            String publishDate, BigDecimal priceStandard, BigDecimal priceSales, String categoryName) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.priceStandard = priceStandard;
        this.priceSales = priceSales;
        this.categoryName = categoryName;
    }
}
