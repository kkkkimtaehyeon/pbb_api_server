package com.nhnacademy.shop.deliveryAddress.repository;

import com.nhnacademy.shop.deliveryAddress.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    @Query("SELECT da FROM DeliveryAddress da WHERE da.member.id = :memberId AND da.isDefault IS TRUE ")
    Optional<DeliveryAddress> findByMemberIdAndIsDefaultTrue(@Param("memberId") Long memberId);
    Optional<DeliveryAddress> findByIdAndMember_Id(Long deliveryAddressId, Long memberId);
    boolean existsByIdAndMember_Id(Long deliveryAddressId, Long memberId);
    List<DeliveryAddress> findAllByMember_Id(Long memberId);
}
