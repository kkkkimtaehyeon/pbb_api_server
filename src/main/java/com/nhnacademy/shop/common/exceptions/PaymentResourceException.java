package com.nhnacademy.shop.common.exceptions;

public class PaymentResourceException extends RuntimeException{
    public PaymentResourceException(String message) {
        super(message);
    }

    public PaymentResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
