package com.nhnacademy.shop.stock.strategy.redis;

import com.nhnacademy.shop.order.v2.dto.OrderItemCreationRequest;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.product.repository.ProductRepository;
import com.nhnacademy.shop.stock.strategy.StockDeductionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Redis MULTI/EXEC 트랜잭션 재고차감 전략.
 * WATCH → 재고확인 → MULTI → DECRBY → EXEC 흐름 구현.
 * WATCH 이후 다른 클라이언트가 키를 변경하면 EXEC가 null을 반환 → 재시도(최대 3회).
 *
 * SessionCallback을 익명 클래스로 구현(람다는 제네릭 메서드 때문에 컴파일 안 됨).
 */
@Slf4j
@RequiredArgsConstructor
@Component("REDIS_TRANSACTION")
public class RedisTransactionStockStrategy implements StockDeductionStrategy {

    private static final String STOCK_KEY_PREFIX = "stock:";
    private static final int MAX_RETRY = 3;

    private final RedisTemplate<String, Object> orderRedisTemplate;
    private final ProductRepository productRepository;

    @Override
    public void deduct(List<OrderItemCreationRequest> items) {
        for (OrderItemCreationRequest item : items) {
            String key = STOCK_KEY_PREFIX + item.productId();
            ensureStockInRedis(key, item.productId());
            deductWithTransaction(key, item.productId(), item.quantity());
        }
    }

    private void deductWithTransaction(String key, Long productId, int quantity) {
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            // 재고 부족 여부는 트랜잭션 외부에서 확인
            Object stockObj = orderRedisTemplate.opsForValue().get(key);
            if (stockObj == null) {
                throw new IllegalArgumentException("Redis 재고 키가 없습니다: " + key);
            }
            int currentStock = Integer.parseInt(stockObj.toString());
            if (currentStock < quantity) {
                throw new IllegalStateException(
                        String.format("재고가 부족합니다. 상품ID: %d, 재고: %d, 요청: %d",
                                productId, currentStock, quantity));
            }

            // WATCH → MULTI → DECRBY → EXEC (익명 클래스로 SessionCallback 구현)
            @SuppressWarnings("unchecked")
            List<Object> results = (List<Object>) orderRedisTemplate.execute(
                    new SessionCallback<List<Object>>() {
                        @Override
                        public <K, V> List<Object> execute(RedisOperations<K, V> ops) {
                            @SuppressWarnings("unchecked")
                            RedisOperations<String, Object> redisOps = (RedisOperations<String, Object>) ops;
                            redisOps.watch(key);
                            redisOps.multi();
                            redisOps.opsForValue().decrement(key, quantity);
                            return redisOps.exec();
                        }
                    });

            if (results != null && !results.isEmpty()) {
                log.debug("[Redis Transaction] 재고차감 성공: key={}, 시도={}", key, attempt + 1);
                return;
            }
            log.warn("[Redis Transaction] EXEC 실패(충돌), 재시도 {}/{}: key={}", attempt + 1, MAX_RETRY, key);
        }
        throw new IllegalStateException("Redis 트랜잭션 재고차감 실패(최대 재시도 초과): key=" + key);
    }

    @Override
    public void rollback(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            String key = STOCK_KEY_PREFIX + orderItem.getProduct().getId();
            orderRedisTemplate.opsForValue().increment(key, orderItem.getQuantity());
            log.debug("[Redis Transaction] 재고롤백: key={}", key);
        }
    }

    @Override
    public String strategyType() {
        return "REDIS_TRANSACTION";
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
