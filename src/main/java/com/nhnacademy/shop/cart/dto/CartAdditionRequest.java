package com.nhnacademy.shop.cart.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartAdditionRequest {
    private List<CartItemAdditionRequest> items;
}


