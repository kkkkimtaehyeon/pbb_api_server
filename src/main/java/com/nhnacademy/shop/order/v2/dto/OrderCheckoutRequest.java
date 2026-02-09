package com.nhnacademy.shop.order.v2.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCheckoutRequest {
    @NotNull
    private List<OrderItemCreationRequest> orderProducts;
    @NotNull
    private BigDecimal amount;
    @Nullable
    private Long paymentIntentId;
    @Nullable
    private Long orderId;
}
