package com.nhnacademy.shop.order.v2.repository;

import com.nhnacademy.shop.order.v2.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT o FROM orders o WHERE o.member.id = :memberId ORDER BY o.orderedAt DESC ")
    Page<Order> findAllByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT o FROM orders o  WHERE o.id = :orderId AND o.member.id = :memberId")
    Optional<Order> findByOrderIdAndMemberId(@Param("orderId") String orderId, @Param("memberId") Long memberId);

}
