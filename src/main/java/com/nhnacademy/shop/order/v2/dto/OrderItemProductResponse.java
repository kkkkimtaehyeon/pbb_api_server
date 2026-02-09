package com.nhnacademy.shop.order.v2.dto;

import com.nhnacademy.shop.product.entity.Product;

public record OrderItemProductResponse(
        Long productId,
        String name,
        String imageUrl
) {
    public static OrderItemProductResponse from(Product product) {
        return new OrderItemProductResponse(
                product.getId(),
                product.getName(),
                product.getImageUrl()
        );
    }
}