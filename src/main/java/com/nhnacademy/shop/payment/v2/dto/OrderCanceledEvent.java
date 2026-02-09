package com.nhnacademy.shop.payment.v2.dto;

import com.nhnacademy.shop.order.v2.entity.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Getter
public class OrderCanceledEvent {
    @NotNull
    private String orderId;
    private List<OrderItem> cancelItems;
    @NotBlank
    private String cancelReason;
    @NotNull
    private BigDecimal cancelAmount;
}
