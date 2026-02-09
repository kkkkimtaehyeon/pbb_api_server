package com.nhnacademy.shop.payment.v2.service;


import com.nhnacademy.shop.common.enums.PaymentIntentStatus;
import com.nhnacademy.shop.common.enums.PaymentType;
import com.nhnacademy.shop.order.v2.entity.Order;
import com.nhnacademy.shop.order.v2.repository.OrderRepository;
import com.nhnacademy.shop.order.v2.service.OrderService;
import com.nhnacademy.shop.payment.v2.dto.*;
import com.nhnacademy.shop.payment.v2.entity.Payment;
import com.nhnacademy.shop.payment.v2.entity.PaymentIntent;
import com.nhnacademy.shop.payment.v2.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentIntentService paymentIntentService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentValidationService paymentValidationService;
    private final PgService pgService;

    public PaymentIntentResponse processPaymentIntent(Long memberId, PaymentRequest request) {
        String orderId = request.getOrderId();
        BigDecimal amount = request.getAmount();
        orderService.validateOrderMember(memberId, orderId);
        PaymentIntent currentPi = orderService.getCurrentPaymentIntent(orderId);
        // 결제의도가 없으면 새로 저장
        if (currentPi == null) {
            return paymentIntentService.createPaymentIntent(orderId, amount);
        }
        // 결제의도가 있으면 상태 분기처리
        return paymentIntentService.resolvePaymentIntent(orderId, amount);
    }

    @Transactional
    public void savePaymentConfirm(PaymentSaveRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문 정보가 존재하지 않습니다."));
        Payment payment = Payment.builder()
                .paymentKey(request.getPaymentKey())
                .type(PaymentType.CONFIRM)
                .requestedAt(request.getRequestedAt())
                .approvedAt(request.getApprovedAt())
                .amount(request.getAmount())
                .order(order)
                .build();
        paymentRepository.save(payment);
    }
//    1. 데드락 발생
//    @Transactional
//    public Long completePaymentConfirm(PaymentConfirmResponse response, Long paymentIntentId) {
//        Orders order = orderRepository.findById(response.getOrderId())
//                .orElseThrow(() -> new RuntimeException("order not found"));
//
//        Long paymentId = savePayment(PaymentSaveRequest.from(response));
//        // 결제의도 상태 변경
//        paymentIntentService.updateStatus(paymentIntentId, PaymentIntentStatus.DONE);
//        // 주문 상태 변경
//        order.setStatus(OrderStatus.PAYMENT_COMPLETED);
//        return paymentId;
//    }

    // 2. 데드락 발생
//    @Transactional
//    public void completePaymentConfirm(PaymentConfirmResponse response, Long paymentIntentId) {
//        Orders order = orderRepository.findById(response.getOrderId())
//                .orElseThrow(() -> new RuntimeException("order not found"));
//        // 1. 결제 정보 저장
//        Payment payment = PaymentSaveRequest.from(response).toEntity(order);
//        paymentRepository.save(payment);
//        // 2. 결제의도 상태 변경
//        paymentIntentService.updateStatus(paymentIntentId, PaymentIntentStatus.DONE);
//        // 3. 주문 상태 변경
//        order.setStatus(OrderStatus.PAYMENT_COMPLETED);
//    }

    @Transactional
    public Long startPaymentConfirm(String orderId, BigDecimal amount) {
        Long paymentIntentId = paymentValidationService.validateConfirmPayment(orderId, amount);
        // 결제 의도가 "처리중" 임을 저장
        paymentIntentService.updateStatus(paymentIntentId, PaymentIntentStatus.PROCESSING);
        return paymentIntentId;
    }

    public void cancel(PaymentCancelCommand command) {
//        Payment payment = paymentRepository.findByPaymentKey(command.getPaymentKey())
//                .orElseThrow(() -> new IllegalArgumentException("결제정보가 존재하지 않습니다."));
//        String paymentKey = payment.getPaymentKey();
        // pg사 결제취소 요청
        PaymentCancelResponse response = pgService.cancelPayment(command.getPaymentKey(), command.getCancelReason(), command.getCancelAmount());

        // 결제취소 정보 저장
        Payment cancelPayment = Payment.builder()
                .type(PaymentType.CANCEL)
                .requestedAt(response.getRequestedAt())
                .cancelledAt(response.getCanceledAt())
                .amount(response.getCancelAmount())
                .paymentKey(response.getPaymentKey())
                .build();
        paymentRepository.save(cancelPayment);
    }
}
