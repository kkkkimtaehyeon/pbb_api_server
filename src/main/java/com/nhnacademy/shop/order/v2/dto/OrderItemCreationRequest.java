package com.nhnacademy.shop.order.v2.dto;

import java.math.BigDecimal;

public record OrderItemCreationRequest(
        Long productId,
        BigDecimal discountAmount,
        BigDecimal price,
        Integer quantity
) {
}
