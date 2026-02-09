package com.nhnacademy.shop.payment.v2.entity;

import com.nhnacademy.shop.common.enums.PaymentIntentStatus;
import com.nhnacademy.shop.order.v2.entity.Order;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class PaymentIntent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column
    @Enumerated(EnumType.STRING)
    private PaymentIntentStatus status;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private BigDecimal amount;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Builder
    public PaymentIntent(Long id, PaymentIntentStatus status, Order order, LocalDateTime createdAt, BigDecimal amount) {
        this.id = id;
        this.status = status;
        this.order = order;
        this.createdAt = createdAt;
        this.amount = amount;
    }

    public boolean isConfirmable() {
        return this.status == PaymentIntentStatus.REQUIRES_PAYMENT;
    }

    public void processing() {
        this.status = PaymentIntentStatus.PROCESSING;
    }

    public void ready() {
        this.status = PaymentIntentStatus.READY;
    }

    public void succeeded() {
        this.status = PaymentIntentStatus.DONE;
    }

    public void failed() {
        this.status = PaymentIntentStatus.FAILED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(10));
    }

}
