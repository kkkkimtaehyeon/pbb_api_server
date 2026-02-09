package com.nhnacademy.shop.order.claim.dto;

import lombok.Data;

@Data
public class OrderCancelRequest {
    String cancelReason;
    int cancelQuantity;
}

