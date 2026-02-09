package com.nhnacademy.shop.common.enums;

import lombok.Getter;

@Getter
public enum PaymentTransactionStatus {
    PROGRESSING,

    PENDING,
    SUCCEEDED,
    FAILED
}
