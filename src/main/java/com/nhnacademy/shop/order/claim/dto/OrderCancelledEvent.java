package com.nhnacademy.shop.order.claim.dto;

public record OrderCancelledEvent(
        Long orderClaimId,
        Long productId,
        int rollbackQuantity
) {
}
