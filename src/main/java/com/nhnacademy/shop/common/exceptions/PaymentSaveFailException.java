package com.nhnacademy.shop.common.exceptions;

public class PaymentSaveFailException extends RuntimeException{
    public PaymentSaveFailException(String message) {
        super(message);
    }
}
