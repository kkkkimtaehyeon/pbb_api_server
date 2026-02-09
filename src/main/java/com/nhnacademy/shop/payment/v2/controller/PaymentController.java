package com.nhnacademy.shop.payment.v2.controller;


import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.order.v2.dto.OrderCreationRequest;
import com.nhnacademy.shop.payment.v2.dto.PaymentConfirmRequest;
import com.nhnacademy.shop.payment.v2.dto.PaymentConfirmResponse;
import com.nhnacademy.shop.payment.v2.dto.PaymentIntentResponse;
import com.nhnacademy.shop.payment.v2.service.PaymentFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v2/payments")
@RequiredArgsConstructor
@RestController
public class PaymentController {
    private final PaymentFacade paymentFacade;

//    @PostMapping
//    public ResponseEntity<PaymentIntentResponse> payment(@Valid @RequestBody PaymentRequest request) {
//        PaymentIntentResponse response = paymentService.processPaymentIntent(memberId, request);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping
    public ResponseEntity<PaymentIntentResponse> payment(@AuthenticationPrincipal MemberDetail memberDetail,
                                                         @Valid @RequestBody OrderCreationRequest request) {
        Long memberId = memberDetail.getMemberId();
        PaymentIntentResponse response = paymentFacade.createOrderAndPaymentIntent(memberId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> paymentConfirm(@Valid @RequestBody PaymentConfirmRequest request) {
        PaymentConfirmResponse response = paymentFacade.confirmPayment(request);
        return ResponseEntity.ok(response);
    }
}
