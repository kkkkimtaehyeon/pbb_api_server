package com.nhnacademy.shop.payment.v2.service;


import com.nhnacademy.shop.common.enums.OrderItemStatus;
import com.nhnacademy.shop.common.enums.PaymentIntentStatus;
import com.nhnacademy.shop.common.exceptions.PaymentFailException;
import com.nhnacademy.shop.common.exceptions.PgClientException;
import com.nhnacademy.shop.common.exceptions.PgException;
import com.nhnacademy.shop.common.exceptions.PgServerException;
import com.nhnacademy.shop.order.v2.dto.OrderCreationRequest;
import com.nhnacademy.shop.order.v2.dto.OrderCreationResponse;
import com.nhnacademy.shop.order.v2.service.OrderFacade;
import com.nhnacademy.shop.order.v2.service.OrderService;
import com.nhnacademy.shop.payment.v2.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentFacade {
    private final PaymentService paymentService;
    private final PaymentIntentService paymentIntentService;
    private final PgService pgService;
    private final OrderService orderService;
    private final OrderFacade orderFacade;

    public PaymentIntentResponse createOrderAndPaymentIntent(Long memberId, OrderCreationRequest request) {
        OrderCreationResponse orderCreationResponse = orderFacade.createOrder(memberId, request);
        PaymentRequest paymentRequest = new PaymentRequest(orderCreationResponse.orderId(), orderCreationResponse.paymentAmount());
        return paymentService.processPaymentIntent(memberId, paymentRequest);
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        String orderId = request.getOrderId();
        Long paymentIntentId = paymentService.startPaymentConfirm(orderId, request.getAmount());
        // PG 사에 결제 승인 요청
        PaymentConfirmResponse response = null;
        try {
            response = pgService.confirmPayment(request, paymentIntentId);
            // payment 정보 저장
//            paymentService.completePaymentConfirm(response, paymentIntentId);
            completePaymentConfirm(response, paymentIntentId);
        } catch (PgException pgException) {
            handlePgException(paymentIntentId, pgException);
        }
        return response;
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
