package com.nhnacademy.shop.order.v2.entity;

import com.nhnacademy.shop.common.enums.OrderClaimStatus;
import com.nhnacademy.shop.common.enums.OrderClaimType;
import com.nhnacademy.shop.member.v2.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class OrderClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderClaimType type;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderClaimStatus status;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = true)
    private String returnTrackingNumber;

    @Column
    private BigDecimal cancelAmount;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime refundedAt;

    @Column
    private LocalDateTime stockRolledBackAt;

    @Column
    private LocalDateTime completedAt;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", referencedColumnName = "id")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Builder
    public OrderClaim(Long id, OrderClaimType type, OrderClaimStatus status, String reason, String returnTrackingNumber, BigDecimal cancelAmount, LocalDateTime createdAt, LocalDateTime completedAt, LocalDateTime refundedAt, OrderItem orderItem, Member member) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.reason = reason;
        this.returnTrackingNumber = returnTrackingNumber;
        this.cancelAmount = cancelAmount;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.refundedAt = refundedAt;
        this.orderItem = orderItem;
        this.member = member;
    }

    public static OrderClaim cancel(OrderItem orderItem,
                                    String reason,
                                    BigDecimal cancelAmount) {
        return OrderClaim.builder()
                .type(OrderClaimType.CANCEL)
                .status(OrderClaimStatus.REQUESTED)
                .reason(reason)
                .cancelAmount(cancelAmount)
                .orderItem(orderItem)
                .build();
    }

    public static OrderClaim returns(String reason,
                                     String returnTrackingNumber,
                                     BigDecimal returnAmount,
                                     OrderItem orderItem,
                                     Member member) {
        return OrderClaim.builder()
                .type(OrderClaimType.RETURNS)
                .status(OrderClaimStatus.REQUESTED)
                .reason(reason)
                .returnTrackingNumber(returnTrackingNumber)
                .cancelAmount(returnAmount)
                .orderItem(orderItem)
                .member(member)
                .build();
    }

    public boolean isReturnConfirmable() {
        return status == OrderClaimStatus.REQUESTED && orderItem.isReturnConfirmable();
    }

    public void refunded() {
        status = OrderClaimStatus.REFUNDED;
        refundedAt = LocalDateTime.now();
        tryComplete();
    }

    public void stockRolledBack() {
        stockRolledBackAt = LocalDateTime.now();
        tryComplete();
    }

    private void tryComplete() {
        if (refundedAt != null && stockRolledBackAt != null) {
            this.status = OrderClaimStatus.COMPLETED;
            // 반품요청된 주문이 완료되면 반품완료로 상태 변경
            if (type == OrderClaimType.RETURNS) {
                orderItem.completeReturn();
            }
        }
    }
}
