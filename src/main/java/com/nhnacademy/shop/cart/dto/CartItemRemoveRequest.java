package com.nhnacademy.shop.cart.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartItemRemoveRequest {
    List<Long> cartItemIds;
}
