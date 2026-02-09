package com.nhnacademy.shop.common.exceptions;

public class PaymentCancelFailException extends RuntimeException {
    public PaymentCancelFailException(String message) {
        super(message);
    }

    public PaymentCancelFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
