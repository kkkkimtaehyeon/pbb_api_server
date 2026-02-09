package com.nhnacademy.shop.payment.v2.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TossPaymentResponse {
    private final String orderId;
    private final String status;
    private final String paymentKey;
    private final String requestedAt;
    private final String approvedAt;
    private final BigDecimal totalAmount;
    private final List<TossPaymentCancelResponse> cancels;

    @Builder
    public TossPaymentResponse(String orderId, String status, String paymentKey, String requestedAt, String approvedAt, BigDecimal totalAmount, List<TossPaymentCancelResponse> cancels) {
        this.orderId = orderId;
        this.status = status;
        this.paymentKey = paymentKey;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
        this.cancels = cancels;
    }

    public boolean isCanceled() {
        return cancels != null && !cancels.isEmpty() && cancels.getLast().getCancelStatus().equals("DONE");
    }

    public boolean isSucceeded() {
        return status.equals("DONE");
    }

    public static TossPaymentResponse mockResponse(String orderId, String paymentKey, BigDecimal amount) {
        return TossPaymentResponse.builder()
                .status("DONE")
                .approvedAt(String.valueOf(LocalDateTime.now()))
                .requestedAt(String.valueOf(LocalDateTime.now()))
                .cancels(null)
                .paymentKey(paymentKey)
                .totalAmount(amount)
                .orderId(orderId)
                .build();
    }

    public static TossPaymentResponse mockCancelResponse(String paymentKey, BigDecimal cancelAmount, String cancelReason) {
        return TossPaymentResponse.builder()
                .status("DONE")
                .approvedAt(String.valueOf(LocalDateTime.now()))
                .requestedAt(String.valueOf(LocalDateTime.now()))
                .cancels(null)
                .paymentKey(paymentKey)
                .cancels(List.of(TossPaymentCancelResponse.mockResponse(cancelAmount, cancelReason)))
                .build();
    }
}

