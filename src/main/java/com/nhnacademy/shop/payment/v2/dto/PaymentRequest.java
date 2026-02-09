package com.nhnacademy.shop.payment.v2.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    String orderId;
    BigDecimal amount;

    public PaymentRequest(String orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
}
