package com.nhnacademy.shop.order.v2.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreationResponse(
        String orderId,
        BigDecimal paymentAmount
) {
}


