package com.nhnacademy.shop.order.v2.dto;

import java.math.BigDecimal;

public record OrderClaimCompletedEvent(
        Long orderClaimId,
        String orderId,
        Long orderItemId,
        Long productId,
        BigDecimal cancelAmount,
        String cancelReason,
        int cancelQuantity
) {
}
