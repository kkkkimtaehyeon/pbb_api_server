package com.nhnacademy.shop.payment.v2.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentIntentResponse {
    private String status; // "PAYMENT_REQUIRED", "ALREADY_PAID", "IN_PROCESS", "FAILED"
    private String orderId;
    private BigDecimal amount;
    private String message;

    public PaymentIntentResponse(String orderId, BigDecimal amount, String status, String message) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.message = message;
    }
}
