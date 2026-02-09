package com.nhnacademy.shop.review.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewResponse {
    String memberName;
    String content;
    Integer star;
    List<String> fileUrls;

    public ReviewResponse(String memberName, String content, Integer star, List<String> fileUrls) {
        this.memberName = memberName;
        this.content = content;
        this.star = star;
        this.fileUrls = fileUrls;
    }
}
