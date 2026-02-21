package com.nhnacademy.shop.payment.v2.service;

import com.nhnacademy.shop.common.enums.OrderItemStatus;
import com.nhnacademy.shop.common.enums.PaymentIntentStatus;
import com.nhnacademy.shop.common.exceptions.PaymentFailException;
import com.nhnacademy.shop.common.exceptions.PgClientException;
import com.nhnacademy.shop.common.exceptions.PgException;
import com.nhnacademy.shop.common.exceptions.PgServerException;
import com.nhnacademy.shop.order.v2.entity.Order;
import com.nhnacademy.shop.order.v2.service.OrderService;
import com.nhnacademy.shop.payment.v2.dto.*;
import com.nhnacademy.shop.stock.strategy.StockDeductionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentFacade {
    private final PaymentService paymentService;
    private final PaymentIntentService paymentIntentService;
    private final PgService pgService;
    private final OrderService orderService;
    private final Map<String, StockDeductionStrategy> stockStrategies;

    public PaymentIntentResponse createPaymentIntent(Long memberId, PaymentRequest request) {
        return paymentService.processPaymentIntent(memberId, request);
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        String orderId = request.getOrderId();
        Long paymentIntentId = paymentService.startPaymentConfirm(orderId, request.getAmount());
        // PG 사에 결제 승인 요청
        PaymentConfirmResponse response = null;
        try {
            response = pgService.confirmPayment(request, paymentIntentId);
            completePaymentConfirm(response, paymentIntentId);
        } catch (PgException pgException) {
            // 결제 실패 → 재고 롤백
            rollbackStock(orderId);
            handlePgException(paymentIntentId, pgException);
        }
        return response;
    }

    /**
     * 결제 실패 시 주문의 strategyType을 읽어 해당 전략으로 재고를 복원합니다.
     */
    private void rollbackStock(String orderId) {
        try {
            Order order = orderService.findOrder(orderId);
            String strategyType = order.getStrategyType();
            if (strategyType == null) {
                log.warn("[Stock Rollback] strategyType이 없습니다. orderId={}", orderId);
                return;
            }
            StockDeductionStrategy strategy = stockStrategies.get(strategyType);
            if (strategy == null) {
                log.warn("[Stock Rollback] 알 수 없는 전략 타입: {}", strategyType);
                return;
            }
            strategy.rollback(order.getOrderItems());
            log.info("[Stock Rollback] 재고 복원 완료: orderId={}, strategy={}", orderId, strategyType);
        } catch (Exception e) {
            // 롤백 실패는 로그만 남기고 기존 결제 실패 처리를 방해하지 않음
            log.error("[Stock Rollback] 재고 복원 실패: orderId={}", orderId, e);
        }
    }

    private void handlePgException(Long paymentIntentId, PgException e) {
        if (e instanceof PgClientException clientException) {
            if (clientException.isRetryable()) {
                paymentIntentService.updateStatus(paymentIntentId, PaymentIntentStatus.READY);
            } else {
                // 재시도 불가능한 사용자 에러는 FAILED 처리
                paymentIntentService.updateStatus(paymentIntentId, PaymentIntentStatus.FAILED);
            }
            throw new PaymentFailException(clientException);
        } else {
            PgServerException serverException = (PgServerException) e;
            paymentIntentService.updateStatus(paymentIntentId, PaymentIntentStatus.READY);
            throw new PaymentFailException(serverException);
        }
    }

    public void completePaymentConfirm(PaymentConfirmResponse response, Long paymentIntentId) {
        paymentService.savePaymentConfirm(PaymentSaveRequest.from(response));
        // 결제의도 상태 변경
        paymentIntentService.updateStatus(paymentIntentId, PaymentIntentStatus.DONE);
        orderService.updateOrderProductStatus(response.getOrderId(), OrderItemStatus.PAYMENT_COMPLETED);
    }
}
