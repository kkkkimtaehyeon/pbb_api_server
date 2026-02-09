package com.nhnacademy.shop.deliveryAddress.repository;

import com.nhnacademy.shop.deliveryAddress.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    Optional<MemberAddress> findByMemberIdAndDeliveryAddressId(Long memberId, Long deliveryAddressId);

    boolean existsByMemberIdAndDeliveryAddressId(Long memberId, Long deliveryAddressId);

    void deleteByMemberIdAndDeliveryAddressId(Long memberId, Long deliveryAddressId);

    List<MemberAddress> findAllByMemberId(Long memberId);

    Optional<MemberAddress> findByMemberIdAndIsDefaultTrue(Long memberId);
}
