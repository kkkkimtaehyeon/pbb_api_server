package com.nhnacademy.shop.stock.strategy.db;

import com.nhnacademy.shop.order.v2.dto.OrderItemCreationRequest;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.product.entity.Product;
import com.nhnacademy.shop.product.repository.ProductRepository;
import com.nhnacademy.shop.stock.strategy.StockDeductionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DB 기반 낙관적락(Optimistic Lock) 재고차감 전략.
 * SELECT ... (version 체크) → 재고 확인/차감 → UPDATE (version 불일치 시 예외)
 * 충돌 발생 시 최대 3회 재시도 (지수 백오프 100ms).
 */
@RequiredArgsConstructor
@Component("DB_OPTIMISTIC")
public class DbOptimisticStockStrategy implements StockDeductionStrategy {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 100, multiplier = 2))
    public void deduct(List<OrderItemCreationRequest> items) {
        List<Long> productIds = items.stream().map(OrderItemCreationRequest::productId).toList();
        List<Product> products = productRepository.findAllByIdWithOptimisticLock(productIds);

        for (OrderItemCreationRequest item : items) {
            Product product = products.stream()
                    .filter(p -> p.getId().equals(item.productId()))
                    .findFirst()
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
        return "DB_OPTIMISTIC";
    }
}
