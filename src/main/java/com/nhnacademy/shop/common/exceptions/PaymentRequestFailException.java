package com.nhnacademy.shop.common.exceptions;

public class PaymentRequestFailException extends RuntimeException {
    public PaymentRequestFailException(String message) {
        super(message);
    }
}
