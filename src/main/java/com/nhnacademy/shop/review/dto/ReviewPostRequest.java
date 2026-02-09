package com.nhnacademy.shop.review.dto;

import lombok.Data;

@Data
public class ReviewPostRequest {
    private String content;
    private Integer star;
}
