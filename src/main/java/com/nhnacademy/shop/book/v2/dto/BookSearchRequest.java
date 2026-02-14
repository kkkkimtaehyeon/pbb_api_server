package com.nhnacademy.shop.book.v2.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class BookSearchRequest {
    @Nullable Long categoryId;

}
