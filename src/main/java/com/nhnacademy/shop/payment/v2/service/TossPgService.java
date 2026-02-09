package com.nhnacademy.shop.payment.v2.service;


import com.nhnacademy.shop.common.exceptions.PgClientException;
import com.nhnacademy.shop.common.exceptions.PgServerException;
import com.nhnacademy.shop.payment.v2.dto.PaymentCancelResponse;
import com.nhnacademy.shop.payment.v2.dto.PaymentConfirmRequest;
import com.nhnacademy.shop.payment.v2.dto.PaymentConfirmResponse;
import com.nhnacademy.shop.payment.v2.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class TossPgService implements PgService {
    private final TossPaymentClient toss;
    private final PaymentTransactionService paymentTxService;

    @Override
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request, Long paymentIntentId) {
        // 주문 및 상태 검증
        String orderId = request.getOrderId();

        // 결제 트랜잭션 시작
        Long txId = paymentTxService.startTransaction(paymentIntentId);

        TossPaymentResponse tossResponse = null;
        try {
            tossResponse = toss.mockConfirmPayment(orderId, request.getPaymentKey(), request.getAmount());
//            tossResponse = toss.confirmPayment(orderId, request.getPaymentKey(), request.getAmount());
            // 결제 트랜잭션 끝 (성공처리)
            paymentTxService.endTransaction(txId);
        } catch (PgClientException clientException) {
            paymentTxService.failTransaction(txId, clientException);
            clientException.setRetryable(isRetryableError(clientException.getErrorCode()));
            throw clientException;
        } catch (PgServerException serverException) {
            // PG 서버 에러는 재시도 할 수 있도록 READY 처리
            paymentTxService.failTransaction(txId, serverException);
            serverException.setRetryable(isRetryableError(serverException.getErrorCode()));
            throw serverException;
        }

        // 공통 응답형식으로 토스 응답을 반환
        return PaymentConfirmResponse.from(tossResponse);
    }

    @Override
    public PaymentConfirmResponse fetchPaymentByOrderId(String orderId) {
        TossPaymentResponse tossResponse = toss.mockFetchPayment(orderId);
        return PaymentConfirmResponse.from(tossResponse);
    }

    @Override
    public PaymentCancelResponse cancelPayment(String paymentKey, String reason, BigDecimal cancelAmount) {
        TossPaymentResponse response = toss.mockCancelPayment(paymentKey, cancelAmount, reason);
        return PaymentCancelResponse.from(response);
    }

    private boolean isRetryableError(String errorCode) {
        return switch (errorCode) {
            // 1. 이미 처리되었거나 찾을 수 없는 경우 (재시도 의미 없음)
            case "ALREADY_PROCESSED_PAYMENT", "NOT_FOUND_PAYMENT", "NOT_FOUND_PAYMENT_SESSION" -> false;

            // 2. 연동 및 인증 오류 (코드 수정이나 설정 변경 필요)
            case "INVALID_API_KEY", "UNAUTHORIZED_KEY", "INVALID_REQUEST", "INCORRECT_BASIC_AUTH_FORMAT" -> false;

            // 3. 비즈니스 규칙 위반 (금액 미달 등)
            case "BELOW_MINIMUM_AMOUNT", "NOT_REGISTERED_BUSINESS", "INVALID_UNREGISTERED_SUBMALL" -> false;

            // 4. 보안 정책 (FDS 차단 등은 해당 세션에서 재시도 불가한 경우가 많음)
            case "FDS_ERROR", "FORBIDDEN_REQUEST" -> false;

            // 나머지는 모두 READY로 두어 재시도 허용
            default -> true;
        };
    }


}
