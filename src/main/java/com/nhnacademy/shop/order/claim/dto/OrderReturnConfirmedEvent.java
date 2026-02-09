package com.nhnacademy.shop.order.claim.dto;

public record OrderReturnConfirmedEvent(
        Long orderClaimId,
        Long productId,
        int rollbackQuantity
) {
}
