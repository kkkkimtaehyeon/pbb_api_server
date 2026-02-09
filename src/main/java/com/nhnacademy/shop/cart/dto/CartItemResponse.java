package com.nhnacademy.shop.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    Long cartItemId;
    Long productId;
    String productImageUrl;
    String productName;
    BigDecimal price;
    int quantity;

    public CartItemResponse(Long cartItemId, Long productId, String productImageUrl, String productName, BigDecimal price, int quantity) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productImageUrl = productImageUrl;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
}
