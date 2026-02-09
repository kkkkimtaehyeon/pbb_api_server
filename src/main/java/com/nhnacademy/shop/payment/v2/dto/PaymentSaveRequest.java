package com.nhnacademy.shop.payment.v2.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentSaveRequest {
    String paymentKey;
    String orderId;
    BigDecimal amount;
    LocalDateTime requestedAt;
    LocalDateTime approvedAt;

    @Builder
    public PaymentSaveRequest(String paymentKey, String orderId, BigDecimal amount, LocalDateTime requestedAt, LocalDateTime approvedAt) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public static PaymentSaveRequest from(PaymentConfirmResponse response) {
        return PaymentSaveRequest.builder()
                .paymentKey(response.getPaymentKey())
                .orderId(response.getOrderId())
                .amount(response.getAmount())
                .requestedAt(LocalDateTime.parse(response.getRequestedAt()))
                .approvedAt(LocalDateTime.parse(response.getApprovedAt()))
                .build();
    }

}
