package com.nhnacademy.shop.common.exceptions;

import lombok.Getter;

@Getter
public class PgException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public PgException(String errorCode, String errorMessage, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
