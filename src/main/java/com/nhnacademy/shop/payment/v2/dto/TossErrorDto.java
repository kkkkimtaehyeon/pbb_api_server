package com.nhnacademy.shop.payment.v2.dto;

import lombok.Data;

@Data
public class TossErrorDto {
    String code;
    String message;

    public TossErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
