package com.nhnacademy.shop.deliveryAddress.entity;

import com.nhnacademy.shop.member.v2.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class DeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    String receiver;

    @Column(nullable = false)
    String phoneNumber;

    @Column(nullable = false)
    String zipcode;

    @Column(nullable = false)
    String address;

    @Column(nullable = false, length = 500)
    String addressDetail;

    @Column(nullable = false)
    boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Builder
    public DeliveryAddress(Long id, String receiver, String phoneNumber, String zipcode, String address, String addressDetail, boolean isDefault, Member member) {
        this.id = id;
        this.receiver = receiver;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
        this.member = member;
    }


    public void update(String receiver, String phoneNumber, String zipcode, String address, String addressDetail) {
        this.receiver = receiver;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public void updateDefault(boolean b) {
        isDefault = b;
    }
}
