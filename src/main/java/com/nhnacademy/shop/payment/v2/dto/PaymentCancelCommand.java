package com.nhnacademy.shop.payment.v2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCancelCommand {
    String orderId;
    @NotNull
    private String paymentKey;
    @NotNull
    private BigDecimal cancelAmount;
    @NotBlank
    private String cancelReason;

    public PaymentCancelCommand(String paymentKey, BigDecimal cancelAmount, String cancelReason) {
        this.paymentKey = paymentKey;
        this.cancelAmount = cancelAmount;
        this.cancelReason = cancelReason;
    }

    public PaymentCancelCommand(String orderId, String paymentKey, BigDecimal cancelAmount, String cancelReason) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.cancelAmount = cancelAmount;
        this.cancelReason = cancelReason;
    }
}
