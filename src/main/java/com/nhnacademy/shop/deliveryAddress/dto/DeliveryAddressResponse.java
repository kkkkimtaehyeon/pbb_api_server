package com.nhnacademy.shop.deliveryAddress.dto;

import com.nhnacademy.shop.deliveryAddress.entity.DeliveryAddress;
import com.nhnacademy.shop.deliveryAddress.entity.MemberAddress;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeliveryAddressResponse {
    private Long id;
    private String receiver;
    private String phoneNumber;
    private String zipcode;
    private String address;
    private String addressDetail;
    private Boolean isDefault;

    @Builder
    public DeliveryAddressResponse(Long id, String receiver, String phoneNumber, String zipcode, String address,
            String addressDetail, Boolean isDefault) {
        this.id = id;
        this.receiver = receiver;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public static DeliveryAddressResponse from(DeliveryAddress deliveryAddress) {
        return DeliveryAddressResponse.builder()
                .id(deliveryAddress.getId())
                .receiver(deliveryAddress.getReceiver())
                .phoneNumber(deliveryAddress.getPhoneNumber())
                .zipcode(deliveryAddress.getZipcode())
                .address(deliveryAddress.getAddress())
                .addressDetail(deliveryAddress.getAddressDetail())
                .isDefault(deliveryAddress.isDefault())
                .build();
    }
}
