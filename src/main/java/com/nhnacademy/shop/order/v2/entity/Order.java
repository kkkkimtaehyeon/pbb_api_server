package com.nhnacademy.shop.order.v2.entity;

import com.nhnacademy.shop.delivery.entity.OrderDelivery;
import com.nhnacademy.shop.deliveryAddress.entity.DeliveryAddress;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.payment.v2.entity.Payment;
import com.nhnacademy.shop.payment.v2.entity.PaymentIntent;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @Setter
    @Column
    private int usedPoint;

    @Setter
    @Column
    private BigDecimal paymentAmount;

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressDetail;

    //    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "current_pi_id", referencedColumnName = "id")
    private PaymentIntent currentPaymentIntent;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Payment> payments;


    @Builder
    public Order(String id, LocalDateTime orderedAt, int usedPoint, BigDecimal paymentAmount, String receiver, String phoneNumber, String zipcode, String address, String addressDetail, List<OrderItem> orderItems, Member member, PaymentIntent currentPaymentIntent) {
        this.id = id;
        this.orderedAt = orderedAt;
        this.usedPoint = usedPoint;
        this.paymentAmount = paymentAmount;
        this.receiver = receiver;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.orderItems = orderItems;
        this.member = member;
        this.currentPaymentIntent = currentPaymentIntent;
    }


    public static Order createPendingOrder(Member member, DeliveryAddress deliveryAddress, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .receiver(deliveryAddress.getReceiver())
                .phoneNumber(deliveryAddress.getPhoneNumber())
                .zipcode(deliveryAddress.getZipcode())
                .address(deliveryAddress.getAddress())
                .addressDetail(deliveryAddress.getAddressDetail())
                .usedPoint(0)
                .member(member)
                .build();
        for (OrderItem orderItem : orderItems) {
            order.addItem(orderItem);
        }
        order.calculatePaymentAmount();
        return order;
    }

    public void addItem(OrderItem item) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        item.setOrder(this);
        orderItems.add(item);
    }

    public BigDecimal calculatePaymentAmount() {
        // 주문 상품 합계
        BigDecimal orderItemSum = orderItems.stream()
                .map(OrderItem::getPaymentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 사용 포인트 차감
        paymentAmount = orderItemSum.subtract(BigDecimal.valueOf(usedPoint));
        return paymentAmount;
    }

    public void setCurrentPaymentIntent(PaymentIntent paymentIntent) {
        paymentIntent.setOrder(this);
        this.currentPaymentIntent = paymentIntent;
    }
}
