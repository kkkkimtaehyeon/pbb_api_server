package com.nhnacademy.shop.payment.v2.entity;

import com.nhnacademy.shop.common.enums.PaymentType;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Column(nullable = false, length = 200)
    private String paymentKey;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private BigDecimal amount;

    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
    private Order order;

    @Builder
    public Payment(Long id, PaymentType type, String paymentKey, LocalDateTime requestedAt, LocalDateTime completedAt, BigDecimal amount, Order order) {
        this.id = id;
        this.type = type;
        this.paymentKey = paymentKey;
        this.requestedAt = requestedAt;
        this.completedAt = completedAt;
        this.amount = amount;
        this.order = order;
    }
}
