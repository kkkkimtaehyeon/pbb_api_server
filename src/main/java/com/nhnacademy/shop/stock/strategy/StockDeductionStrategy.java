package com.nhnacademy.shop.stock.strategy;

import com.nhnacademy.shop.order.v2.dto.OrderItemCreationRequest;
import com.nhnacademy.shop.order.v2.entity.OrderItem;

import java.util.List;

/**
 * 재고 차감/롤백 전략 인터페이스.
 * 주문 시 재고를 선점(차감)하고, 결제 실패 시 롤백합니다.
 */
public interface StockDeductionStrategy {

    /**
     * 주문 생성 시 재고를 선점(차감)합니다.
     * 
     * @param items 주문 아이템 목록
     */
    void deduct(List<OrderItemCreationRequest> items);

    /**
     * 결제 실패 시 선점한 재고를 복원합니다.
     * 
     * @param orderItems 주문 아이템 엔티티 목록
     */
    void rollback(List<OrderItem> orderItems);

    /**
     * 전략 식별자 (Order.strategyType 에 저장됩니다)
     */
    String strategyType();
}
