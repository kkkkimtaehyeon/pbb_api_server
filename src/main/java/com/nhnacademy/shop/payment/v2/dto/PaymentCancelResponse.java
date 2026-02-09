package com.nhnacademy.shop.payment.v2.dto;

import com.nhnacademy.shop.payment.v2.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PaymentCancelResponse {
    String paymentKey;
    LocalDateTime requestedAt;
    LocalDateTime canceledAt;
    BigDecimal cancelAmount;
    private String cancelStatus;

    public static PaymentCancelResponse from(TossPaymentResponse toss) {
        return new PaymentCancelResponse(
                toss.getPaymentKey(),
                LocalDateTime.parse(toss.getRequestedAt()),
                toss.getCancels().getLast().getCanceledAt().toLocalDateTime(),
                BigDecimal.valueOf(toss.getCancels().getLast().getCancelAmount()),
                toss.getStatus()

        );
    }
}
