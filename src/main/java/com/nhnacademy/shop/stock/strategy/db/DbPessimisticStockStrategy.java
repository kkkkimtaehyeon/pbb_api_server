package com.nhnacademy.shop.stock.strategy.db;

import com.nhnacademy.shop.order.v2.dto.OrderItemCreationRequest;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.product.entity.Product;
import com.nhnacademy.shop.product.repository.ProductRepository;
import com.nhnacademy.shop.stock.strategy.StockDeductionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * DB 기반 비관적락(Pessimistic Lock) 재고차감 전략.
 * SELECT ... FOR UPDATE → 재고 확인/차감 → UPDATE
 * 다른 트랜잭션은 락이 해제될 때까지 대기.
 * 데드락 방지를 위해 상품 ID 오름차순으로 정렬 후 락 획득.
 */
@RequiredArgsConstructor
@Component("DB_PESSIMISTIC")
public class DbPessimisticStockStrategy implements StockDeductionStrategy {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void deduct(List<OrderItemCreationRequest> items) {
        List<OrderItemCreationRequest> sortedItems = items.stream()
                .sorted(Comparator.comparingLong(OrderItemCreationRequest::productId))
                .toList();
        for (OrderItemCreationRequest item : sortedItems) {
            Product product = productRepository.findByIdWithPessimisticLock(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("상품정보가 존재하지 않습니다."));
            product.deductStock(item.quantity());
        }
    }

    @Override
    @Transactional
    public void rollback(List<OrderItem> orderItems) {
        List<Long> productIds = orderItems.stream()
                .map(oi -> oi.getProduct().getId())
                .sorted()
                .toList();

        List<Product> products = productRepository.findAllByIdWithPessimisticLock(productIds);

        for (OrderItem orderItem : orderItems) {
            products.stream()
                    .filter(p -> p.getId().equals(orderItem.getProduct().getId()))
                    .findFirst()
                    .ifPresent(p -> p.addStock(orderItem.getQuantity()));
        }
    }

    @Override
    public String strategyType() {
        return "DB_PESSIMISTIC";
    }
}
