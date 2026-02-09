package com.nhnacademy.shop.payment.v2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentConfirmRequest {
    @NotNull
    private final String paymentKey;
    @NotNull
    @Min(1)
    private final BigDecimal amount;
    @NotNull
    private final String orderId;

    public PaymentConfirmRequest(String paymentKey, BigDecimal amount, String orderId) {
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.orderId = orderId;
    }
}
