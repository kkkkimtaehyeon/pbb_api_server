package com.nhnacademy.shop.stock.strategy.redis;

import com.nhnacademy.shop.order.v2.dto.OrderItemCreationRequest;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.product.repository.ProductRepository;
import com.nhnacademy.shop.stock.strategy.StockDeductionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Redis 기반 단순 재고차감 전략.
 * GET → 재고 확인 → SET (비원자성 - Race Condition 가능)
 * 동시성 문제가 있으나 Redis 기반 관리 패턴의 시작점.
 * Redis 재고 없으면 DB에서 로드.
 */
@Slf4j
@RequiredArgsConstructor
@Component("REDIS_SIMPLE")
public class RedisSimpleStockStrategy implements StockDeductionStrategy {

    private static final String STOCK_KEY_PREFIX = "stock:";

    private final RedisTemplate<String, Object> orderRedisTemplate;
    private final ProductRepository productRepository;

    @Override
    public void deduct(List<OrderItemCreationRequest> items) {
        for (OrderItemCreationRequest item : items) {
            String key = STOCK_KEY_PREFIX + item.productId();
            // Redis에 재고가 없으면 DB에서 초기화
            ensureStockInRedis(key, item.productId());

            Object stockObj = orderRedisTemplate.opsForValue().get(key);
            int currentStock = Integer.parseInt(stockObj.toString());

            if (currentStock < item.quantity()) {
                throw new IllegalStateException(
                        String.format("재고가 부족합니다. 상품ID: %d, 재고: %d, 요청: %d",
                                item.productId(), currentStock, item.quantity()));
            }
            orderRedisTemplate.opsForValue().set(key, currentStock - item.quantity());
        }
    }

    @Override
    public void rollback(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            String key = STOCK_KEY_PREFIX + orderItem.getProduct().getId();
            ensureStockInRedis(key, orderItem.getProduct().getId());

            Object stockObj = orderRedisTemplate.opsForValue().get(key);
            int currentStock = Integer.parseInt(stockObj.toString());
            orderRedisTemplate.opsForValue().set(key, currentStock + orderItem.getQuantity());
        }
    }

    @Override
    public String strategyType() {
        return "REDIS_SIMPLE";
    }

    private void ensureStockInRedis(String key, Long productId) {
        if (Boolean.FALSE.equals(orderRedisTemplate.hasKey(key))) {
            int dbStock = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품정보가 존재하지 않습니다. id=" + productId))
                    .getStock();
            orderRedisTemplate.opsForValue().set(key, dbStock);
            log.info("[Redis] DB에서 재고 초기화: key={}, stock={}", key, dbStock);
        }
    }
}
