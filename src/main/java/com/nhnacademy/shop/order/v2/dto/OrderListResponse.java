package com.nhnacademy.shop.order.v2.dto;

import com.nhnacademy.shop.order.v2.entity.Order;

import java.time.LocalDate;
import java.util.List;

public record OrderListResponse(
        String orderId,
        LocalDate orderDate,
        List<OrderItemResponse> items
) {
    public static OrderListResponse from(Order order) {
        return new OrderListResponse(
                order.getId(),
                order.getOrderedAt().toLocalDate(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}

