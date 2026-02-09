package com.nhnacademy.shop.payment.v2.service;


import com.nhnacademy.shop.common.enums.PaymentTransactionStatus;
import com.nhnacademy.shop.common.exceptions.PgException;
import com.nhnacademy.shop.payment.v2.entity.PaymentIntent;
import com.nhnacademy.shop.payment.v2.entity.PaymentTransaction;
import com.nhnacademy.shop.payment.v2.repository.PaymentIntentRepository;
import com.nhnacademy.shop.payment.v2.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTxRepository;
    private final PaymentIntentRepository paymentIntentRepository;

    @Transactional(readOnly = true)
    public PaymentTransaction getPaymentTx(Long paymentIntentId) {
        return paymentTxRepository
                .findLatestTxByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 트랜잭션 정보가 존재하지 않습니다."));
    }

    @Transactional
    public void updateStatus(Long id, PaymentTransactionStatus status) {
        PaymentTransaction tx = paymentTxRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("결제 트랜잭션 정보가 존재하지 않습니다."));
        tx.setStatus(status);
    }


    @Transactional
    public Long startTransaction(Long paymentIntentId) {
        // 결제의도 검증 및 상태 검증
        PaymentIntent paymentIntent = paymentIntentRepository.findById(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("결제의도가 존재하지 않습니다."));

        // 결제 트랜잭션 저장
        PaymentTransaction paymentTx = PaymentTransaction.builder()
                .status(PaymentTransactionStatus.PROGRESSING)
                .createdAt(LocalDateTime.now())
                .paymentIntent(paymentIntent)
                .build();
        paymentTxRepository.save(paymentTx);
        return paymentTx.getId();
    }

    @Transactional
    public void failTransaction(Long txId, PgException e) {
        // 결제 트랜잭션 실패 처리
        PaymentTransaction paymentTx = paymentTxRepository.findById(txId)
                .orElseThrow(() -> new RuntimeException("결제 트랜잭션이 존재하지 않습니다."));
        paymentTx.fail(e.getErrorCode(), e.getErrorMessage(), e);

    }

    @Transactional
    public void endTransaction(Long txId) {
        // 결제 트랜잭션 성공 처리
        PaymentTransaction paymentTx = paymentTxRepository.findById(txId)
                .orElseThrow(() -> new RuntimeException("결제 트랜잭션이 존재하지 않습니다."));
        paymentTx.success();
    }
}
