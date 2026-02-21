package com.nhnacademy.shop.stock.strategy.redis;

import com.nhnacademy.shop.order.v2.dto.OrderItemCreationRequest;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.product.repository.ProductRepository;
import com.nhnacademy.shop.stock.strategy.StockDeductionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Redis Lua Script 재고차감 전략.
 * Lua 스크립트는 Redis에서 원자적으로 실행되므로 중간에 다른 명령 끼어들기 불가.
 *
 * 스크립트 동작:
 * 1. 현재 재고 조회 (tonumber(redis.call('GET', key)))
 * 2. 키가 없으면 -1 반환
 * 3. 재고가 요청 수량보다 적으면 -2 반환
 * 4. 충분하면 DECRBY 실행 후 남은 재고 반환
 */
@Slf4j
@RequiredArgsConstructor
@Component("REDIS_LUA")
public class RedisLuaStockStrategy implements StockDeductionStrategy {

    private static final String STOCK_KEY_PREFIX = "stock:";

    // Lua 스크립트: 원자적 재고차감
    // KEYS[1] = 재고 키, ARGV[1] = 차감 수량
    // 반환값: 남은 재고 (양수) | -1 (키 없음) | -2 (재고 부족)
    private static final String DEDUCT_SCRIPT = "local stock = tonumber(redis.call('GET', KEYS[1])) " +
            "if stock == nil then return -1 end " +
            "if stock < tonumber(ARGV[1]) then return -2 end " +
            "return redis.call('DECRBY', KEYS[1], ARGV[1])";

    // Lua 스크립트: 원자적 재고복원 (INCRBY)
    private static final String ROLLBACK_SCRIPT = "return redis.call('INCRBY', KEYS[1], ARGV[1])";

    private final RedisTemplate<String, Object> orderRedisTemplate;
    private final ProductRepository productRepository;

    @Override
    public void deduct(List<OrderItemCreationRequest> items) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(DEDUCT_SCRIPT, Long.class);

        for (OrderItemCreationRequest item : items) {
            String key = STOCK_KEY_PREFIX + item.productId();
            ensureStockInRedis(key, item.productId());

            Long result = orderRedisTemplate.execute(
                    script,
                    Collections.singletonList(key),
                    (long) item.quantity());

            if (result == null || result == -1L) {
                throw new IllegalArgumentException("Redis 재고 키가 존재하지 않습니다: " + key);
            }
            if (result == -2L) {
                throw new IllegalStateException(
                        String.format("재고가 부족합니다. 상품ID: %d, 요청 수량: %d", item.productId(), item.quantity()));
            }
            log.debug("[Redis Lua] 재고차감 완료: key={}, 남은재고={}", key, result);
        }
    }

    @Override
    public void rollback(List<OrderItem> orderItems) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(ROLLBACK_SCRIPT, Long.class);

        for (OrderItem orderItem : orderItems) {
            String key = STOCK_KEY_PREFIX + orderItem.getProduct().getId();
            Long result = orderRedisTemplate.execute(
                    script,
                    Collections.singletonList(key),
                    (long) orderItem.getQuantity());
            log.debug("[Redis Lua] 재고롤백 완료: key={}, 복원후재고={}", key, result);
        }
    }

    @Override
    public String strategyType() {
        return "REDIS_LUA";
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
