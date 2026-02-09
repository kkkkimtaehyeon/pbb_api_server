package com.nhnacademy.shop.delivery.dto;

import lombok.Data;

@Data
public class DeliveryDetailResponse {
    String company;
    String trackingNumber;

    public DeliveryDetailResponse(String company, String trackingNumber) {
        this.company = company;
        this.trackingNumber = trackingNumber;
    }
}
