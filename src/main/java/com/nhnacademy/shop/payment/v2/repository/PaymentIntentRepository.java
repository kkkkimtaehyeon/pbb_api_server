package com.nhnacademy.shop.payment.v2.repository;

import com.nhnacademy.shop.payment.v2.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {

    @Query("SELECT pi FROM PaymentIntent pi WHERE pi.order.id = :orderId")
    Optional<PaymentIntent> findByOrderId(@Param("orderId") String orderId);
}
