package com.nhnacademy.shop.order.v2.repository;


import com.nhnacademy.shop.order.v2.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByIdAndOrder_IdAndOrder_Member_Id(Long orderItemId,
                                                              String orderId,
                                                              Long memberId);
}
