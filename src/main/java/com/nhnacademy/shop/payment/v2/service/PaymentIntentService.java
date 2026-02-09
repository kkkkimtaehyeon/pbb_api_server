package com.nhnacademy.shop.payment.v2.service;


import com.nhnacademy.shop.common.enums.PaymentIntentStatus;
import com.nhnacademy.shop.common.enums.PaymentTransactionStatus;
import com.nhnacademy.shop.order.v2.entity.Order;
import com.nhnacademy.shop.order.v2.repository.OrderRepository;
import com.nhnacademy.shop.order.v2.service.OrderService;
import com.nhnacademy.shop.payment.v2.dto.PaymentConfirmResponse;
import com.nhnacademy.shop.payment.v2.dto.PaymentIntentResponse;
import com.nhnacademy.shop.payment.v2.entity.PaymentIntent;
import com.nhnacademy.shop.payment.v2.entity.PaymentTransaction;
import com.nhnacademy.shop.payment.v2.repository.PaymentIntentRepository;
import com.nhnacademy.shop.stock.DbStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PaymentIntentService {
    private final OrderService orderService;
    private final DbStockService stockService;
    private final PaymentTransactionService paymentTxService;
    private final PgService pgService;
    private final PaymentIntentRepository paymentIntentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void updateStatus(Long intentId, PaymentIntentStatus status) {
        PaymentIntent intent = paymentIntentRepository.findById(intentId)
                .orElseThrow();
        intent.setStatus(status);
    }

    @Transactional
    public PaymentIntentResponse resolvePaymentIntent(String orderId, BigDecimal amount) {
        PaymentIntent pi = orderService.getCurrentPaymentIntent(orderId);
        // 결제의도가 active한지 확인
        if (pi.getStatus() == PaymentIntentStatus.CANCELED) {
            return new PaymentIntentResponse(orderId, amount, PaymentIntentStatus.CANCELED.toString(), "결제세션이 만료되었습니다.");
        }
        // 만료 검증
        if (pi.isExpired()) {
            pi.setStatus(PaymentIntentStatus.CANCELED);
            // 재고 복구
//            stockService.rollbackQuantity();
        }
        // 주문금액 변경 검증
        if (isAmountChanged(pi.getAmount(), amount)) {
            // 기존 PI 상태 CANCELED 처리, order amount 수정, 새로운 PI 생성
            pi.setStatus(PaymentIntentStatus.CANCELED);
            return createPaymentIntent(orderId, amount);
        }

        PaymentIntentStatus piStatus = pi.getStatus();
        switch (piStatus) {
            case REQUIRES_PAYMENT -> {
                return new PaymentIntentResponse(orderId, amount, PaymentIntentStatus.REQUIRES_PAYMENT.name(), "결제를 진행해주세요.");
            }
            case SUCCEEDED -> {
                return new PaymentIntentResponse(orderId, amount, "ALREADY_PAID", "이미 결제가 성공되었습니다.");
            }
            case CANCELED -> {
                return new PaymentIntentResponse(orderId, amount, "RE_PAYMENT_REQUIRED", "결제 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
            case PROCESSING -> {
                PaymentTransaction paymentTx = paymentTxService.getPaymentTx(pi.getId());
                PaymentTransactionStatus ptStatus = paymentTx.getStatus();
                switch (ptStatus) {
                    case SUCCEEDED -> {
                        pi.setStatus(PaymentIntentStatus.SUCCEEDED);
                        return new PaymentIntentResponse(orderId, amount, "ALREADY_PAID", "이미 결제가 성공되었습니다.");
                    }
                    case FAILED -> {
                        pi.setStatus(PaymentIntentStatus.REQUIRES_PAYMENT);
                        return new PaymentIntentResponse(orderId, amount, "RE_PAYMENT_REQUIRED", "결제 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                    }
                    case PENDING -> {
                        // PG사 결제 조회 API 호출
                        PaymentConfirmResponse fetchResult = pgService.fetchPaymentByOrderId(orderId);
                        // 조회 결과 성공 → PT 상태 SUCCEEDED 처리
                        if (fetchResult.isSucceeded()) {
                            paymentTx.setStatus(PaymentTransactionStatus.SUCCEEDED);
                            pi.setStatus(PaymentIntentStatus.SUCCEEDED);
                            return new PaymentIntentResponse(orderId, amount, "ALREADY_PAID", "이미 결제가 성공되었습니다.");
                        }
                        // 조회 결과 없음, 실패 → PT 상태 FAILED 처리,  PI 상태  REQUIRES_PAYMENT 처리, 사용자에게 재결제 유도
                        paymentTx.setStatus(PaymentTransactionStatus.FAILED);
                        pi.setStatus(PaymentIntentStatus.FAILED);
                        return new PaymentIntentResponse(orderId, amount, "RE_PAYMENT_REQUIRED", "결제 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                    }
                }
            }
        }
        return null;
    }

    @Transactional
    public PaymentIntentResponse createPaymentIntent(String orderId, BigDecimal amount) {
        Order order = orderService.findOrder(orderId);
        PaymentIntent pi = PaymentIntent.builder()
                .status(PaymentIntentStatus.REQUIRES_PAYMENT)
                .amount(amount)
                .order(order)
                .build();
        order.setCurrentPaymentIntent(pi);
        paymentIntentRepository.save(pi);
        orderRepository.save(order); // pi 관계와 저장을 위해 관계 주인을 저장
        // 클라이언트에 결제 UI 렌더링 유도
        return new PaymentIntentResponse(orderId, amount, PaymentIntentStatus.REQUIRES_PAYMENT.toString(), "결제를 진행해주세요.");
    }

    private boolean isAmountChanged(BigDecimal amount1, BigDecimal amount2) {
        return amount1.compareTo(amount2) != 0;
    }

}



