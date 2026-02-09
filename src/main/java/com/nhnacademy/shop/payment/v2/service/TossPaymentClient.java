package com.nhnacademy.shop.payment.v2.service;


import com.nhnacademy.shop.common.exceptions.*;
import com.nhnacademy.shop.payment.v2.dto.TossErrorDto;
import com.nhnacademy.shop.payment.v2.dto.TossPaymentCancelRequest;
import com.nhnacademy.shop.payment.v2.dto.TossPaymentConfirmRequest;
import com.nhnacademy.shop.payment.v2.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.math.BigDecimal;

/**
 * 토스 payment api 에 요청을 보내는 클래스
 */
@RequiredArgsConstructor
@Component
public class TossPaymentClient {
    private final RestTemplate restTemplate;
    private static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/payments/";
    private static final String TOSS_AUTH_KEY = "tossAuthKey";

    @Retryable(
            retryFor = PgServerException.class,
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 300L,
                    random = true,
                    multiplier = 2.0
            )
    )
    public TossPaymentResponse confirmPayment(String orderId, String paymentKey, BigDecimal amount) {
        String url = TOSS_PAYMENT_URL + "confirm";
        TossPaymentConfirmRequest body = new TossPaymentConfirmRequest(paymentKey, amount, orderId);
        TossPaymentResponse response = null;

        try {
            response = restTemplate.postForObject(
                    url,
                    getTossHttpEntity(body),
                    TossPaymentResponse.class
            );
        } catch (HttpClientErrorException e) {
            TossErrorDto tossError = e.getResponseBodyAs(TossErrorDto.class);
            throw new PgClientException(tossError.getCode(), tossError.getMessage(), e);
        } catch (HttpServerErrorException e) {
            TossErrorDto tossError = e.getResponseBodyAs(TossErrorDto.class);
            throw new PgServerException(tossError.getCode(), tossError.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new PgServerException("500", e.getMessage(), e);
        } catch (RestClientException e) {
            throw new PaymentFailException("결제 중 오류 발생", e);
        }

        if (response == null) {
            throw new PaymentFailException("empty response from toss");
        }
        return response;
    }

    @Retryable(
            retryFor = PgServerException.class,
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 300L,
                    random = true,
                    multiplier = 2.0
            )
    )
    public TossPaymentResponse mockConfirmPayment(String orderId, String paymentKey, BigDecimal amount) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
        return TossPaymentResponse.mockResponse(orderId, paymentKey, amount);
    }

    @Retryable(
            retryFor = PgServerException.class,
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 300L,
                    random = true,
                    multiplier = 2.0
            )
    )
    public TossPaymentResponse mockConfirmPaymentWithError30(String orderId, String paymentKey, BigDecimal amount) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
        double error = (Math.random() * 10);
        if (error < 3) {
            throw new PgServerException("500", "error", null);
        }
        return TossPaymentResponse.mockResponse(orderId, paymentKey, amount);
    }

    @Retryable(
            retryFor = PgServerException.class,
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 300L
            )
    )
    public TossPaymentResponse mockConfirmPaymentWithErrorLinearRetry(String orderId, String paymentKey, BigDecimal amount) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
        return TossPaymentResponse.mockResponse(orderId, "paymentKey", BigDecimal.ONE);
    }


    /**
     * 토스 payment를 조회하는 메서드
     *
     * @param paymentKey
     * @return
     */
    public TossPaymentResponse fetchPayment(String paymentKey) {
        String url = TOSS_PAYMENT_URL + paymentKey;
        TossPaymentResponse response = null;
        try {
            response = restTemplate.getForObject(
                    url,
                    TossPaymentResponse.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new PGPaymentNotFoundException(paymentKey);
            } else if (e.getStatusCode().is4xxClientError()) {
                throw new RuntimeException("fail to fetch due to client");
            }
            throw new PaymentFetchFailException("toss fetch api error", e);
        } catch (ResourceAccessException e) {
            throw new PGConnectionException(e);
        }
        if (response == null) {
            throw new PaymentFailException("empty response from toss");
        }
        return response;
    }

    @Retryable(
            retryFor = PgServerException.class,
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 300L,
                    random = true,
                    multiplier = 2.0
            )
    )
    public TossPaymentResponse mockFetchPayment(String orderId) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }
        return TossPaymentResponse.mockResponse(orderId, "paymentKey", BigDecimal.ONE);
    }


    /**
     * 토스 결제를 취소하는 메서드
     */
    public TossPaymentResponse cancelPayment(String paymentKey, BigDecimal amount, String reason) {
        String url = TOSS_PAYMENT_URL + paymentKey + "/cancel";
        TossPaymentCancelRequest cancelRequest = new TossPaymentCancelRequest(
                reason,
                amount
        );

        TossPaymentResponse response = null;
        try {
            response = restTemplate.postForObject(
                    url,
                    getTossHttpEntity(cancelRequest),
                    TossPaymentResponse.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new PaymentCancelFailException("toss cancel api error", e);
        } catch (ResourceAccessException e) {
            throw new PGConnectionException(e);
        }
        if (response == null) {
            throw new PaymentCancelFailException("empty response from toss");
        }
        return response;
    }

    public TossPaymentResponse mockCancelPayment(String paymentKey, BigDecimal amount, String reason) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {

        }
        return TossPaymentResponse.mockCancelResponse(paymentKey, amount, reason);
    }

    private <T> HttpEntity<T> getTossHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(TOSS_AUTH_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }


    private static long randomJitter() {
        return (long) (Math.random() * 100) + 1;
    }


}
