package com.nhnacademy.shop.payment.v2.repository;

import com.nhnacademy.shop.payment.v2.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findFirstByOrder_IdAndTypeAndCompletedAtIsNotNullOrderByCompletedAtAsc(String orderId,
            com.nhnacademy.shop.common.enums.PaymentType type);

    default Optional<Payment> findByOrderId(String orderId) {
        return findFirstByOrder_IdAndTypeAndCompletedAtIsNotNullOrderByCompletedAtAsc(orderId,
                com.nhnacademy.shop.common.enums.PaymentType.CONFIRM);
    }

    java.util.List<Payment> findAllByOrder_Id(String orderId);

    Optional<Payment> findByPaymentKey(String orderId);

}
