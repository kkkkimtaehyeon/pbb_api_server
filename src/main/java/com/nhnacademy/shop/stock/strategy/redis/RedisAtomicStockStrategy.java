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
 * Redis 원자연산(DECRBY) 재고차감 전략.
 * DECRBY는 단일 원자 연산으로, GET-SET 사이의 Race Condition 없음.
 * 차감 후 값이 음수가 되면 재고 부족으로 판단하고 복원 후 예외 발생.
 */
@Slf4j
@RequiredArgsConstructor
@Component("REDIS_ATOMIC")
public class RedisAtomicStockStrategy implements StockDeductionStrategy {

    private static final String STOCK_KEY_PREFIX = "stock:";

    private final RedisTemplate<String, Object> orderRedisTemplate;
    private final ProductRepository productRepository;

    @Override
    public void deduct(List<OrderItemCreationRequest> items) {
        for (OrderItemCreationRequest item : items) {
            String key = STOCK_KEY_PREFIX + item.productId();
            ensureStockInRedis(key, item.productId());

            // 원자적 차감
            Long afterStock = orderRedisTemplate.opsForValue().decrement(key, item.quantity());

            if (afterStock == null || afterStock < 0) {
                // 재고 부족: 원자 연산으로 복원
                orderRedisTemplate.opsForValue().increment(key, item.quantity());
                throw new IllegalStateException(
                        String.format("재고가 부족합니다. 상품ID: %d, 요청 수량: %d",
                                item.productId(), item.quantity()));
            }
            log.debug("[Redis Atomic] 재고차감 완료: key={}, 남은재고={}", key, afterStock);
        }
    }

    @Override
    public void rollback(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            String key = STOCK_KEY_PREFIX + orderItem.getProduct().getId();
            Long restoredStock = orderRedisTemplate.opsForValue()
                    .increment(key, orderItem.getQuantity());
            log.debug("[Redis Atomic] 재고롤백 완료: key={}, 복원후재고={}", key, restoredStock);
        }
    }

    @Override
    public String strategyType() {
        return "REDIS_ATOMIC";
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
