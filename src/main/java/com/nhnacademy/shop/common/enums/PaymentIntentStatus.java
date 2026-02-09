package com.nhnacademy.shop.common.enums;

import lombok.Getter;

@Getter
public enum PaymentIntentStatus {
    READY,
    DONE,
    FAILED,

    REQUIRES_PAYMENT,
    PROCESSING,
    SUCCEEDED,
    CANCELED,

}
