package com.nhnacademy.shop.payment.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class PaymentConfirmResponse {
    private String orderId;
    private String status;
    private String paymentKey;
    private String requestedAt;
    private String approvedAt;
    private BigDecimal amount;

    public static PaymentConfirmResponse from(TossPaymentResponse tossPaymentResponse) {
        return new PaymentConfirmResponse(
                tossPaymentResponse.getOrderId(),
                tossPaymentResponse.getStatus(),
                tossPaymentResponse.getPaymentKey(),
                tossPaymentResponse.getRequestedAt(),
                tossPaymentResponse.getApprovedAt(),
                tossPaymentResponse.getTotalAmount()
        );
    }

    public PaymentConfirmResponse(String orderId, String paymentKey, BigDecimal amount) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public boolean isSucceeded() {
        return this.status.equals("DONE");
    }

    public boolean isFailed() {
        return this.status.equals("ABORTED");
    }
}
