package com.nhnacademy.shop.common.exceptions;

public class PaymentFailException extends RuntimeException{

    public PaymentFailException(PgException pgException) {
        super(pgException.getErrorMessage());
    }

    public PaymentFailException(String message) {
        super(message);
    }

    public PaymentFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
