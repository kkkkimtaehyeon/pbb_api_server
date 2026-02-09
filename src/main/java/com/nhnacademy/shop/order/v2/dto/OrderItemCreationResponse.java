package com.nhnacademy.shop.order.v2.dto;

import com.nhnacademy.shop.common.enums.ProductType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemCreationResponse(
        // orderProduct
        int quantity,
        BigDecimal price,
        BigDecimal discountAmount,
        // product
        Long productId,
        String productName,
        String productImageUrl,
        ProductType productType
) {
}