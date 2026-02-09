package com.nhnacademy.shop.order.v2.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCheckoutResponse {
    private String status; // "PAYMENT_REQUIRED", "ALREADY_PAID", "IN_PROCESS", "FAILED"
    private Long orderId;
    private BigDecimal amount;
    private String message;
}
