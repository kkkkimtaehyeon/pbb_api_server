package com.nhnacademy.shop.payment.v2.service;


import com.nhnacademy.shop.payment.v2.dto.PaymentCancelResponse;
import com.nhnacademy.shop.payment.v2.dto.PaymentConfirmRequest;
import com.nhnacademy.shop.payment.v2.dto.PaymentConfirmResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface PgService {

    PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request, Long paymentIntentId);

    PaymentConfirmResponse fetchPaymentByOrderId(String orderId);

    PaymentCancelResponse cancelPayment(String paymentKey, String reason, BigDecimal cancelAmount);

}
