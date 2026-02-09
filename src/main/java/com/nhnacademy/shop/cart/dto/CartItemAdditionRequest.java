package com.nhnacademy.shop.cart.dto;

import lombok.Data;

@Data
public class CartItemAdditionRequest {
    private Long productId;
    private int quantity;
}