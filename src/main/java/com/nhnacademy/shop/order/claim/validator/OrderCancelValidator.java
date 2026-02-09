package com.nhnacademy.shop.order.claim.validator;

import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.order.v2.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderCancelValidator {
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderItem validate(Long memberId, String orderId, Long orderItemId) {
        // 주문검증
        OrderItem item = orderItemRepository.findByIdAndOrder_IdAndOrder_Member_Id(orderItemId, orderId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("주문상품정보가 존재하지 않습니다."));
        if (!item.isCancellable()) {
            throw new IllegalArgumentException("주문취소는 결제대기/결제완료인 주문상품만 가능합니다.");
        }
        return item;
    }
}
