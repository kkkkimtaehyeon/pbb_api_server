package com.nhnacademy.shop.order.claim.service;

import com.nhnacademy.shop.order.claim.dto.OrderRefundCommand;
import com.nhnacademy.shop.order.claim.repository.OrderClaimRepository;
import com.nhnacademy.shop.order.v2.entity.OrderClaim;
import com.nhnacademy.shop.payment.v2.dto.PaymentCancelCommand;
import com.nhnacademy.shop.payment.v2.entity.Payment;
import com.nhnacademy.shop.payment.v2.repository.PaymentRepository;
import com.nhnacademy.shop.payment.v2.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderRefundService {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final OrderClaimRepository orderClaimRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refund(OrderRefundCommand command) {
        Long orderClaimId = command.orderClaimId();
        OrderClaim orderClaim = orderClaimRepository.findById(orderClaimId)
                .orElseThrow();

        String orderId = orderClaim.getOrderItem().getOrder().getId();
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow();
        paymentService.cancel(new PaymentCancelCommand(payment.getPaymentKey(), orderClaim.getCancelAmount(), orderClaim.getReason()));
        // orderClaim 환불 완료 처리
        orderClaim.refunded();
    }
}
