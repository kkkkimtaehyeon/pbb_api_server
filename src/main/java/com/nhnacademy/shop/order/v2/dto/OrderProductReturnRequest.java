package com.nhnacademy.shop.order.v2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderProductReturnRequest {
    Integer returnQuantity;
    @NotNull
    @NotBlank
    String returnReason;
}
