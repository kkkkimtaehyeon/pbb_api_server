package com.nhnacademy.shop.common.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PgServerException extends PgException {
    private boolean isRetryable;

    public PgServerException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }
}
