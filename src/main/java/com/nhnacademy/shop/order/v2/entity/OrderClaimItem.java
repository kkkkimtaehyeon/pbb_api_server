package com.nhnacademy.shop.order.v2.entity;

import com.nhnacademy.shop.common.enums.OrderClaimItemStatus;
import com.nhnacademy.shop.common.enums.OrderClaimReason;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
public class OrderClaimItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", referencedColumnName = "id")
    private OrderItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_claim_id", referencedColumnName = "id")
    private OrderClaim claim;

    @Column(nullable = false)
    Integer quantity;

    /* ===== 금액 스냅샷 ===== */

    @Column(nullable = false)
    private BigDecimal unitPrice;       // 주문 당시 단가

    @Column(nullable = false)
    private BigDecimal discountAmount;  // 이 아이템에 분배된 할인

    @Column(nullable = false)
    private BigDecimal refundAmount;    // 최종 환불 금액

    /* ===== 상태 & 사유 ===== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderClaimItemStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderClaimReason reason;

    @Column(length = 500)
    private String reasonDetail;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;
}
