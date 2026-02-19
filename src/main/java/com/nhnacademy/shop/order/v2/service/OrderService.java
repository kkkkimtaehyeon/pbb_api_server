package com.nhnacademy.shop.order.v2.service;

import com.nhnacademy.shop.common.enums.OrderItemStatus;
import com.nhnacademy.shop.delivery.entity.OrderDelivery;
import com.nhnacademy.shop.deliveryAddress.entity.DeliveryAddress;
import com.nhnacademy.shop.deliveryAddress.repository.DeliveryAddressRepository;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.member.v2.repository.MemberRepository;
import com.nhnacademy.shop.order.v2.dto.*;
import com.nhnacademy.shop.order.v2.entity.Order;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.order.v2.repository.OrderItemRepository;
import com.nhnacademy.shop.order.v2.repository.OrderRepository;
import com.nhnacademy.shop.payment.v2.entity.Payment;
import com.nhnacademy.shop.payment.v2.entity.PaymentIntent;
import com.nhnacademy.shop.payment.v2.repository.PaymentRepository;
import com.nhnacademy.shop.product.entity.Product;
import com.nhnacademy.shop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
@Service
public class OrderService {
        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;
        private final PaymentRepository paymentRepository;
        private final MemberRepository memberRepository;
        private final DeliveryAddressRepository deliveryAddressRepository;
        private final OrderItemRepository orderItemRepository;

        @Transactional
        public void updateOrderProductStatus(String orderId, OrderItemStatus status) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("order not found"));
                // 주문 상태 변경
                for (OrderItem orderItem : order.getOrderItems()) {
                        orderItem.setStatus(status);
                }
        }

        @Transactional(readOnly = true)
        public PaymentIntent getCurrentPaymentIntent(String orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 존재하지 않습니다."));
                return order.getCurrentPaymentIntent();
        }

        @Transactional(readOnly = true)
        public void validateOrderMember(Long memberId, String orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 존재하지 않습니다."));
                if (!Objects.equals(memberId, order.getMember().getId())) {
                        throw new IllegalArgumentException("주문자 정보가 일치하지 않습니다.");
                }
        }

        @Transactional
        public OrderCreationResponse createPendingOrder(Long memberId, OrderCreationRequest request) {
                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new IllegalArgumentException("회원정보가 존재하지 않습니다."));
                DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.getDeliveryAddressId())
                                .orElseThrow(() -> new IllegalArgumentException("배송지정보가 존재하지 않습니다."));
                // order item 생성
                List<OrderItem> orderItems = request.getItems().stream()
                                .map(item -> {
                                        Product product = productRepository.findById(item.productId())
                                                        .orElseThrow(() -> new IllegalArgumentException(
                                                                        "상품정보가 존재하지 않습니다."));
                                        return OrderItem.create(
                                                        product,
                                                        item.discountAmount(),
                                                        item.quantity());
                                }).toList();
                // order 저장
                Order order = Order.createPendingOrder(member, deliveryAddress, orderItems);
                orderRepository.save(order);
                return new OrderCreationResponse(order.getId(), order.getPaymentAmount());
        }

        @Transactional
        public Order findOrder(String orderId) {
                return orderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 존재하지 않습니다."));
        }

        @Transactional(readOnly = true)
        public Page<OrderListResponse> getOrders(Long memberId, Pageable pageable) {
                Page<Order> orders = orderRepository.findAllByMemberId(memberId, pageable);
                return orders.map(OrderListResponse::from);
        }

        @Transactional(readOnly = true)
        public OrderDetailResponse getOrder(String orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("주문정보가 존재하지 않습니다."));
                List<Payment> payments = paymentRepository.findAllByOrder_Id(orderId);
                OrderPaymentDetailResponse paymentResponse = null;
                if (!payments.isEmpty()) {
                        BigDecimal totalAmount = payments.stream()
                                        .map(p -> p.getType() == com.nhnacademy.shop.common.enums.PaymentType.CANCEL
                                                        ? p.getAmount().negate()
                                                        : p.getAmount())
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        paymentResponse = new OrderPaymentDetailResponse(totalAmount, "toss");
                }
                return OrderDetailResponse.builder()
                                .orderId(order.getId())
                                .amount(order.getPaymentAmount())
                                .receiver(order.getReceiver())
                                .phoneNumber(order.getPhoneNumber())
                                .zipcode(order.getZipcode())
                                .address(order.getAddress())
                                .addressDetail(order.getAddressDetail())
                                .orderedAt(order.getOrderedAt().toLocalDate())
                                .items(order.getOrderItems().stream()
                                                .map(item -> {
                                                        Product product = item.getProduct();
                                                        OrderDelivery orderDelivery = item.getOrderDelivery();
                                                        return OrderItemDetailResponse.builder()
                                                                        .orderItemId(item.getId())
                                                                        .price(item.getPrice())
                                                                        .quantity(item.getQuantity())
                                                                        .discountAmount(item.getDiscountAmount())
                                                                        .productId(product.getId())
                                                                        .productName(product.getName())
                                                                        .productImageUrl(product.getImageUrl())
                                                                        .status(item.getStatus())
                                                                        .orderDelivery(orderDelivery)
                                                                        .build();
                                                })
                                                .toList())
                                .payment(paymentResponse)
                                .build();
        }

        @Transactional
        public void confirmPurchase(Long memberId, String orderId, Long orderItemId) {
                OrderItem orderItem = orderItemRepository
                                .findByIdAndOrder_IdAndOrder_Member_Id(orderItemId, orderId, memberId)
                                .orElseThrow();
                // 상태검증
                orderItem.confirmPurchase();
        }
}
