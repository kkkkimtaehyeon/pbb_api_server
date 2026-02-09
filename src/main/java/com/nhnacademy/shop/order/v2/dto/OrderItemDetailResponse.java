package com.nhnacademy.shop.order.v2.dto;

import com.nhnacademy.shop.common.enums.OrderItemStatus;
import com.nhnacademy.shop.delivery.entity.OrderDelivery;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDetailResponse {
    // orderItem
    Long orderItemId;
    int quantity;
    BigDecimal price;
    BigDecimal discountAmount;
    //product
    Long productId;
    String productName;
    String productImageUrl;
    OrderItemStatusResponse status;
    // delivery
    private DeliveryInfo delivery;

    @Builder
    public OrderItemDetailResponse(Long orderItemId, int quantity, BigDecimal price, BigDecimal discountAmount, Long productId, String productName, String productImageUrl, OrderItemStatus status, OrderDelivery orderDelivery) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.price = price;
        this.discountAmount = discountAmount;
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.status = new OrderItemStatusResponse(status);
        if (orderDelivery != null) {
            this.delivery = new DeliveryInfo(orderDelivery);
        }
    }


}

record DeliveryInfo(String company, String trackingNumber) {
    public DeliveryInfo(OrderDelivery orderDelivery) {
        this(orderDelivery.getCompany(), orderDelivery.getTrackingNumber());
    }
}