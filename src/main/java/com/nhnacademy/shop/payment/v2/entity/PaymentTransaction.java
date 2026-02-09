package com.nhnacademy.shop.payment.v2.entity;

import com.nhnacademy.shop.common.enums.PaymentTransactionStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_intent_id", referencedColumnName = "id")
    PaymentIntent paymentIntent;

    @Setter
    @Column
    @Enumerated(EnumType.STRING)
    PaymentTransactionStatus status;

    @Column
    String errorCode;

    @Column
    String errorMessage;

    @Column
    String rawResponse;

    @Column
    private LocalDateTime createdAt;

    @Builder
    public PaymentTransaction(Long id, PaymentIntent paymentIntent, PaymentTransactionStatus status, String errorCode, String errorMessage, String rawResponse, LocalDateTime createdAt) {
        this.id = id;
        this.paymentIntent = paymentIntent;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.rawResponse = rawResponse;
        this.createdAt = createdAt;
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        // 트랜잭션 생성 후 30분이 지나면 만료처리
        return createdAt.plusMinutes(30).isBefore(now);
    }

    public void fail(String errorCode, String errorMessage, Throwable e) {
        this.status = PaymentTransactionStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.rawResponse = e.getMessage(); // 디버깅을 위한 디테일 에러 메세지
    }

    public void success() {
        this.status = PaymentTransactionStatus.SUCCEEDED;
    }
}
