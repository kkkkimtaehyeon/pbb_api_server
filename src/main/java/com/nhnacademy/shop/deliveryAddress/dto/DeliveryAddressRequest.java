package com.nhnacademy.shop.deliveryAddress.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeliveryAddressRequest {
    private String receiver;
    private String phoneNumber;
    private String zipcode;
    private String address;
    private String addressDetail;
    private Boolean isDefault;

    @Builder
    public DeliveryAddressRequest(String receiver, String phoneNumber, String zipcode, String address,
            String addressDetail, Boolean isDefault) {
        this.receiver = receiver;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }
}
