package com.nhnacademy.shop.payment.v2.dto;

import java.math.BigDecimal;

public record TossPaymentCancelRequest(
        String cancelReasons,
        BigDecimal cancelAmount
){
    public TossPaymentCancelRequest(String cancelReasons, BigDecimal cancelAmount) {
        this.cancelReasons = cancelReasons;
        this.cancelAmount = cancelAmount;
    }

    public static TossPaymentCancelRequest from(OrderCanceledEvent request) {
        return new TossPaymentCancelRequest(request.getCancelReason(), request.getCancelAmount());
    }

}