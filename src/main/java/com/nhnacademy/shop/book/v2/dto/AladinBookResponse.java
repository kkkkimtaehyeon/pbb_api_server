package com.nhnacademy.shop.book.v2.dto;

import lombok.Data;

@Data
public class AladinBookResponse {
    String title;
    String author;
    String pubDate;
    String description;
    String isbn13;
    String priceSales;
    String priceStandard;
    String cover;
    String categoryName;
    String publisher;
    String mallType;
}
