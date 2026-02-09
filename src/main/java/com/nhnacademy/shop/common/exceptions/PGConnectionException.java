package com.nhnacademy.shop.common.exceptions;

public class PGConnectionException extends RuntimeException {

    public PGConnectionException(Throwable cause) {
        super(cause);
    }

    public PGConnectionException(String message) {
        super(message);
    }

    public PGConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
