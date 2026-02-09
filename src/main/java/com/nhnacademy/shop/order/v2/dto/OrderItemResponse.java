package com.nhnacademy.shop.order.v2.dto;

import com.nhnacademy.shop.common.enums.OrderItemStatus;
import com.nhnacademy.shop.order.v2.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long orderItemId,
        OrderItemStatusResponse status,
        Integer quantity,
        BigDecimal price,
        OrderItemProductResponse product
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                new OrderItemStatusResponse(item.getStatus()),
                item.getQuantity(),
                item.getPrice(),
                OrderItemProductResponse.from(item.getProduct())
        );
    }
}

record OrderItemStatusResponse (
        String status,
        String displayName
) {
    public OrderItemStatusResponse(OrderItemStatus status) {
        this(status.getStatus(), status.getDisplayName());
    }
}