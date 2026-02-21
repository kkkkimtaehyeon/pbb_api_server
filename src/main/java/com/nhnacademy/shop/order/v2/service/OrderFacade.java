package com.nhnacademy.shop.order.v2.service;

import com.nhnacademy.shop.order.v2.dto.OrderCreationRequest;
import com.nhnacademy.shop.order.v2.dto.OrderCreationResponse;
import com.nhnacademy.shop.payment.v2.validator.OrderValidator;
import com.nhnacademy.shop.stock.strategy.StockDeductionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OrderFacade {
    private final OrderValidator orderValidator;
    private final OrderService orderService;
    // 전략 빈 이름 → StockDeductionStrategy 맵으로 자동 주입
    private final Map<String, StockDeductionStrategy> stockStrategies;

    /**
     * 기존 호환성 유지 (default: DB_SIMPLE)
     */
    @Transactional
    public OrderCreationResponse createOrder(Long memberId, OrderCreationRequest request) {
        return createOrderWithStrategy(memberId, request, "DB_SIMPLE");
    }

    /**
     * 전략 유형을 지정하여 주문 생성 + 재고 선점
     */
    @Transactional
    public OrderCreationResponse createOrderWithStrategy(Long memberId, OrderCreationRequest request,
            String strategyType) {
        // 상품 검증
        orderValidator.validateCreateOrder(memberId, request);

        // 1. 재고 선점 (전략 선택) - 데드락 방지를 위해 주문 생성보다 먼저 선점
        StockDeductionStrategy strategy = resolveStrategy(strategyType);
        strategy.deduct(request.getItems());

        // 2. 주문 생성 (DB 저장, strategyType 기록)
        OrderCreationResponse response = orderService.createPendingOrderWithStrategy(memberId, request, strategyType);

        return response;
    }

    private StockDeductionStrategy resolveStrategy(String strategyType) {
        StockDeductionStrategy strategy = stockStrategies.get(strategyType);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 재고차감 전략입니다: " + strategyType);
        }
        return strategy;
    }
}
