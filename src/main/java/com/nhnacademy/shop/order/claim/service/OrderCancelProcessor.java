package com.nhnacademy.shop.order.claim.service;

import com.nhnacademy.shop.order.claim.dto.OrderCancelRequest;
import com.nhnacademy.shop.order.claim.dto.OrderRefundCommand;
import com.nhnacademy.shop.order.claim.repository.OrderClaimRepository;
import com.nhnacademy.shop.order.v2.dto.OrderClaimCompletedEvent;
import com.nhnacademy.shop.order.v2.entity.OrderClaim;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.payment.v2.repository.PaymentRepository;
import com.nhnacademy.shop.payment.v2.service.PaymentService;
import com.nhnacademy.shop.stock.StockService;
import com.nhnacademy.shop.stock.dto.StockRollbackCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@RequiredArgsConstructor
@Service
public class OrderCancelProcessor {
    private final OrderClaimRepository orderClaimRepository;

    @Transactional
    public OrderClaim process(OrderItem cancelItem, OrderCancelRequest request) {
        // 주문상품상태 -> 주문취소
        cancelItem.cancel();
        // 주문 취소 정보 저장
        OrderClaim cancelClaim = OrderClaim.cancel(
                cancelItem,
                request.getCancelReason(),
                cancelItem.getPaymentPrice()
        );
        orderClaimRepository.save(cancelClaim);
        return cancelClaim;
    }
}
