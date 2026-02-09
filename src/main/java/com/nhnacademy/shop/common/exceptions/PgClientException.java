package com.nhnacademy.shop.common.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PgClientException extends PgException {
    private boolean retryable;

    public PgClientException(String errorCode, String errorMessage, Throwable cause, boolean retryable) {
        super(errorCode, errorMessage, cause);
        this.retryable = retryable;
    }

    public PgClientException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }
}
