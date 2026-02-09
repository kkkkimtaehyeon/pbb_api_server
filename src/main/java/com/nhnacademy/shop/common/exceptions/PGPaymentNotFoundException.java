package com.nhnacademy.shop.common.exceptions;

public class PGPaymentNotFoundException extends RuntimeException {
    public PGPaymentNotFoundException(String message) {
        super(message);
    }

    public PGPaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
