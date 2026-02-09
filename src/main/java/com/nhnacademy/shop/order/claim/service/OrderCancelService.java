package com.nhnacademy.shop.order.claim.service;

import com.nhnacademy.shop.order.claim.dto.OrderCancelRequest;
import com.nhnacademy.shop.order.claim.dto.OrderCancelledEvent;
import com.nhnacademy.shop.order.v2.entity.OrderClaim;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.order.claim.validator.OrderCancelValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderCancelService {
    private final OrderCancelValidator orderCancelValidator;
    private final OrderCancelProcessor orderCancelProcessor;
    private final ApplicationEventPublisher applicationEventPublisher;

    // 주문취소
    @Transactional
    public void cancelOrder(Long memberId, String orderId, Long orderItemId, OrderCancelRequest request) {
        // 취소할 수 있는 주문인지 검증
        OrderItem cancelItem = orderCancelValidator.validate(memberId, orderId, orderItemId);
        // 주문취소처리
        OrderClaim cancelClaim = orderCancelProcessor.process(cancelItem, request);
        // 결제취소 이벤트 발행
        applicationEventPublisher.publishEvent(new OrderCancelledEvent(
                cancelClaim.getId(),
                cancelClaim.getOrderItem().getProduct().getId(),
                cancelClaim.getOrderItem().getQuantity()
        ));
    }
}
