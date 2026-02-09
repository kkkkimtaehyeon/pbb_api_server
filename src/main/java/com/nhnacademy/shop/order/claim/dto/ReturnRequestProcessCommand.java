package com.nhnacademy.shop.order.claim.dto;

public record ReturnRequestProcessCommand(
        Long memberId,
        Long orderItemId,
        String returnReason,
        Integer returnQuantity
) {
}
