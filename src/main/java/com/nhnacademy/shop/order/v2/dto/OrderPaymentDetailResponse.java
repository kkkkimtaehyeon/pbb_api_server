package com.nhnacademy.shop.order.v2.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderPaymentDetailResponse {
    BigDecimal amount;
    String paymentMethod;

    public OrderPaymentDetailResponse(BigDecimal amount, String paymentMethod) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
}