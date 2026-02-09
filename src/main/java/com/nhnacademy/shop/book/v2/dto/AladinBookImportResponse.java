package com.nhnacademy.shop.book.v2.dto;

import lombok.Data;

import java.util.List;

@Data
public class AladinBookImportResponse {
    List<AladinBookResponse> item;
}
