package com.nhnacademy.shop.payment.v2.service;

import com.nhnacademy.shop.order.v2.entity.Order;
import com.nhnacademy.shop.order.v2.repository.OrderRepository;
import com.nhnacademy.shop.payment.v2.entity.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PaymentValidationService {
    private final OrderRepository orderRepository;

    private void validateAmountEquals(BigDecimal amount1, BigDecimal amount2) {
        if (amount1.compareTo(amount2) != 0) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public PaymentIntent validatePaymentIntent(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 존재하지 않습니다."));
//        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
//            throw new IllegalArgumentException("결제 가능한 주문이 아닙니다.");
//        }
        PaymentIntent paymentIntent = order.getCurrentPaymentIntent();
        if (!paymentIntent.isConfirmable()) {
            throw new RuntimeException("결제를 승인할 수 있는 상태가 아닙니다.");
        }
        return paymentIntent;
    }

    @Transactional(readOnly = true)
    public Long validateConfirmPayment(String orderId, BigDecimal amount) {
        // 결제의도 및 상태 검증
        PaymentIntent paymentIntent = validatePaymentIntent(orderId);
        // 결제금액 검증
        validateAmountEquals(paymentIntent.getAmount(), amount);
        return paymentIntent.getId();
    }
}
