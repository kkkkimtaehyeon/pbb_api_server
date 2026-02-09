package com.nhnacademy.shop.order.v2.dto;

import com.nhnacademy.shop.delivery.entity.OrderDelivery;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDetailResponse {
    // 주문 전체 정보
    private String orderId;
    private BigDecimal amount;
    private LocalDate orderedAt;

    // 주문 배송지 정보
    OrderAddress address;
    // 주문 상품 정보
    List<OrderItemDetailResponse> items;

    // 결제 정보
    OrderPaymentDetailResponse payment;

    @Builder
    public OrderDetailResponse(String orderId, BigDecimal amount, LocalDate orderedAt, String receiver, String phoneNumber, String zipcode, String address, String addressDetail, OrderDelivery orderDelivery, List<OrderItemDetailResponse> items, OrderPaymentDetailResponse payment) {
        this.orderId = orderId;
        this.amount = amount;
        this.orderedAt = orderedAt;
        this.address = new OrderAddress(receiver, phoneNumber, zipcode, address, addressDetail);
        this.items = items;
        this.payment = payment;
    }
}



record OrderAddress(
        String receiver,
        String phoneNumber,
        String zipcode,
        String address,
        String addressDetail
){}
