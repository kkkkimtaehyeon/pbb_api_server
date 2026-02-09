package com.nhnacademy.shop.delivery.entity;

import com.nhnacademy.shop.order.v2.entity.OrderItem;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Entity
public class OrderDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String trackingNumber;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", referencedColumnName = "id")
    private OrderItem orderItem;

    @Builder
    public OrderDelivery(Long id, String company, String trackingNumber, OrderItem orderItem) {
        this.id = id;
        this.company = company;
        this.trackingNumber = trackingNumber;
        this.orderItem = orderItem;
    }
}
