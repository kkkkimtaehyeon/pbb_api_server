package com.nhnacademy.shop.cart.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartResponse {
    List<CartItemResponse> items = new ArrayList<>();

    public CartResponse(List<CartItemResponse> items) {
        this.items = items;
    }
}
