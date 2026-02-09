package com.nhnacademy.shop.order.claim.repository;

import com.nhnacademy.shop.common.enums.OrderClaimType;
import com.nhnacademy.shop.order.v2.entity.OrderClaim;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderClaimRepository extends JpaRepository<OrderClaim, Long> {
    @Query("SELECT oc FROM OrderClaim oc WHERE oc.type = :claimType")
    List<OrderClaim> findAll(@Param("claimType") OrderClaimType claimType, Pageable pageable);
}
