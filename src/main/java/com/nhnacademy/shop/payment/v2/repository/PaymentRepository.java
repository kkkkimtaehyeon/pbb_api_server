package com.nhnacademy.shop.payment.v2.repository;

import com.nhnacademy.shop.payment.v2.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p " +
            "FROM Payment p " +
            "WHERE p.order.id = :orderId AND p.type = 'CONFIRM' AND p.approvedAt IS NOT NULL " +
            "ORDER BY p.approvedAt ASC " +
            "LIMIT 1")
    Optional<Payment> findByOrderId(@Param("orderId") String orderId);

    Optional<Payment> findByPaymentKey(String orderId);

}
