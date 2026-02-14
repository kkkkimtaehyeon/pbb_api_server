package com.nhnacademy.shop.admin.service;

import com.nhnacademy.shop.common.enums.OrderClaimType;
import com.nhnacademy.shop.common.enums.OrderStatus;
import com.nhnacademy.shop.order.claim.dto.OrderClaimListResponse;
import com.nhnacademy.shop.order.claim.repository.OrderClaimQueryRepository;
import com.nhnacademy.shop.order.v2.dto.OrderDetailResponse;
import com.nhnacademy.shop.order.v2.dto.OrderSimpleResponse;
import com.nhnacademy.shop.order.v2.entity.Order;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.order.v2.repository.OrderRepository;
import com.nhnacademy.shop.order.v2.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@PreAuthorize(value = "hasRole('ADMIN')")
@RequiredArgsConstructor
@Service
public class OrderAdminService {
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderClaimQueryRepository orderClaimQueryRepository;


    @Transactional(readOnly = true)
    public Page<OrderSimpleResponse> getAllOrders(Pageable pageable, OrderStatus status) {
        Page<Order> orders = null;
        if (status == null) {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(order -> {
            List<OrderItem> orderItems = order.getOrderItems();
            String productSummary = getProductSummary(orderItems);
            return OrderSimpleResponse.builder()
                    .orderId(order.getId())
                    .orderedAt(order.getOrderedAt().toLocalDate())
                    .amount(order.calculatePaymentAmount())
                    .productSummary(productSummary)
                    .build();
        });
    }

    @Transactional(readOnly = true)
    public String getProductSummary(List<OrderItem> orderItems) {
        int size = orderItems.size();
        String firstProductName = orderItems.getFirst().getProduct().getName();
        if (size == 1) {
            return firstProductName;
        }
        return String.format("%s외 %d건", firstProductName, size - 1);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrder(String id) {
        return orderService.getOrder(id);
    }

    @Transactional(readOnly = true)
    public Page<OrderClaimListResponse> getOrderClaims(OrderClaimType claimType, Pageable pageable) {
        return orderClaimQueryRepository.findAll(claimType, pageable);
    }
}
