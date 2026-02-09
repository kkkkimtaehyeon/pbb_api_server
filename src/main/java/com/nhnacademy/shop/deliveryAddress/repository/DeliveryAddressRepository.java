package com.nhnacademy.shop.deliveryAddress.repository;

import com.nhnacademy.shop.deliveryAddress.dto.DeliveryAddressResponse;
import com.nhnacademy.shop.deliveryAddress.entity.DeliveryAddress;
import com.nhnacademy.shop.deliveryAddress.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    Optional<DeliveryAddress> findByMemberIdAndIsDefaultTrue(Long memberId);
    Optional<DeliveryAddress> findByIdAndMember_Id(Long deliveryAddressId, Long memberId);
    boolean existsByIdAndMember_Id(Long deliveryAddressId, Long memberId);
    List<DeliveryAddress> findAllByMember_Id(Long memberId);
}
