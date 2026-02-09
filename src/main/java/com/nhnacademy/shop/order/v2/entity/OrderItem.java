package com.nhnacademy.shop.order.v2.entity;

import com.nhnacademy.shop.common.enums.OrderItemStatus;
import com.nhnacademy.shop.delivery.entity.OrderDelivery;
import com.nhnacademy.shop.product.entity.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private Integer quantity;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private OrderDelivery orderDelivery;

    @Builder
    public OrderItem(Long id, BigDecimal price, BigDecimal discountAmount, Integer quantity, OrderItemStatus status, Product product, Order order) {
        this.id = id;
        this.price = price;
        this.discountAmount = discountAmount == null ? BigDecimal.ZERO : discountAmount;
        this.quantity = quantity;
        this.status = status;
        this.product = product;
        this.order = order;
    }

    public static OrderItem create(Product product, BigDecimal discountAmount, Integer quantity) {
        return OrderItem.builder()
                .price(product.getPriceSales())
                .discountAmount(discountAmount)
                .quantity(quantity)
                .status(OrderItemStatus.PAYMENT_PENDING)
                .product(product)
                .build();
    }


    public BigDecimal getPaymentPrice() {
        BigDecimal priceSum = price.multiply(BigDecimal.valueOf(quantity));
        return priceSum.subtract(discountAmount);
    }

    public boolean isCancellable() {
        // 결제 전이거나 결제 완료일때만 주문취소 가능
        return status == OrderItemStatus.PAYMENT_PENDING || status == OrderItemStatus.PAYMENT_COMPLETED;
    }

    public boolean isReturnable() {
        // 배송이 완료, TODO: 배송완료 후 일주일 이내
        return status == OrderItemStatus.DELIVERED;
    }

    public boolean isReturnConfirmable() {
        return status == OrderItemStatus.RETURN_REQUESTED;
    }

    public void completeReturn() {
        this.status = OrderItemStatus.RETURN_COMPLETED;
    }

    public void cancel() {
        this.status = OrderItemStatus.ORDER_CANCELLED;
    }

    public void confirmPurchase() {
        if (status != OrderItemStatus.DELIVERED) {
            throw new IllegalArgumentException("구매확정이 가능한 상태가 아닙니다.");
        }
        status = OrderItemStatus.PURCHASE_CONFIRMED;
    }

    public void markAsReturnRequested() {
        if (status != OrderItemStatus.DELIVERED) {
            throw new IllegalArgumentException("반품요청이 가능한 상태가 아닙니다.");
        }
        status = OrderItemStatus.RETURN_REQUESTED;
    }

    public void isDeliveryRegistrable() {
        if (status != OrderItemStatus.PAYMENT_COMPLETED) {
            throw new IllegalArgumentException("배송정보를 등록하려면 결제완료 상태여야합니다.");
        }
    }
    public void shipped(OrderDelivery orderDelivery) {
        this.status = OrderItemStatus.SHIPPED;
        orderDelivery.setOrderItem(this);
        this.orderDelivery = orderDelivery;
    }
}
