package com.nhnacademy.shop.order.v2.service;

import com.nhnacademy.shop.order.v2.dto.OrderCreationRequest;
import com.nhnacademy.shop.order.v2.dto.OrderCreationResponse;
import com.nhnacademy.shop.payment.v2.validator.OrderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderFacade {
    private final OrderValidator orderValidator;
    private final OrderService orderService;

    @Transactional
    public OrderCreationResponse createOrder(Long memberId, OrderCreationRequest request) {
        orderValidator.validateCreateOrder(memberId, request);
        return orderService.createPendingOrder(memberId, request);
    }
}
