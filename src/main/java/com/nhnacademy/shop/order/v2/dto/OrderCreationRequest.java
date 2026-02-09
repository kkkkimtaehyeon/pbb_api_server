package com.nhnacademy.shop.order.v2.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreationRequest {
    @NotNull
    private List<OrderItemCreationRequest> items;
    @NotNull
    private Long deliveryAddressId;
}

