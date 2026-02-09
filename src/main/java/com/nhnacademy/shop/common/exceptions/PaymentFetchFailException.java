package com.nhnacademy.shop.common.exceptions;

public class PaymentFetchFailException extends RuntimeException {
    public PaymentFetchFailException(String message) {
        super(message);
    }

    public PaymentFetchFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
