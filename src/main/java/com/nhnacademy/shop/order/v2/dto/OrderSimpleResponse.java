package com.nhnacademy.shop.order.v2.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderSimpleResponse {
    private String orderId;
    private BigDecimal amount;
    private LocalDate orderedAt;
    private String productSummary;

    @Builder
    public OrderSimpleResponse(String orderId, BigDecimal amount, LocalDate orderedAt, String productSummary) {
        this.orderId = orderId;
        this.amount = amount;
        this.orderedAt = orderedAt;
        this.productSummary = productSummary;
    }
}
