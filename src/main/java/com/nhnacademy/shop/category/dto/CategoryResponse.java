package com.nhnacademy.shop.category.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private List<CategoryResponse> children = new ArrayList<>();

    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
