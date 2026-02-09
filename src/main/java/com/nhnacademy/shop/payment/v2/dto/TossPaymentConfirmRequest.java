package com.nhnacademy.shop.payment.v2.dto;

import java.math.BigDecimal;

public class TossPaymentConfirmRequest extends PaymentConfirmRequest {
    public TossPaymentConfirmRequest(String paymentKey, BigDecimal amount, String orderId) {
        super(paymentKey, amount, orderId);
    }
}
