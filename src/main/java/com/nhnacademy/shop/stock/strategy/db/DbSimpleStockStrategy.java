package com.nhnacademy.shop.stock.strategy.db;

import com.nhnacademy.shop.order.v2.dto.OrderItemCreationRequest;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.product.entity.Product;
import com.nhnacademy.shop.product.repository.ProductRepository;
import com.nhnacademy.shop.stock.strategy.StockDeductionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DB 기반 단순 재고차감 전략.
 * SELECT → 재고 확인 → UPDATE (락 없음)
 * 동시성 제어 없음, 성능이 가장 좋지만 동시 주문 시 재고 음수 가능.
 */
@RequiredArgsConstructor
@Component("DB_SIMPLE")
public class DbSimpleStockStrategy implements StockDeductionStrategy {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void deduct(List<OrderItemCreationRequest> items) {
        for (OrderItemCreationRequest item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("상품정보가 존재하지 않습니다. id=" + item.productId()));
            product.deductStock(item.quantity());
        }
    }

    @Override
    @Transactional
    public void rollback(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findById(orderItem.getProduct().getId())
                    .orElseThrow();
            product.addStock(orderItem.getQuantity());
        }
    }

    @Override
    public String strategyType() {
        return "DB_SIMPLE";
    }
}
