package com.nhnacademy.shop.delivery.repository;

import com.nhnacademy.shop.delivery.entity.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<OrderDelivery, Long> {
}
