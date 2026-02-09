package com.nhnacademy.shop.eventListner;

import com.nhnacademy.shop.order.claim.dto.OrderCancelledEvent;
import com.nhnacademy.shop.order.claim.dto.OrderRefundCommand;
import com.nhnacademy.shop.order.claim.dto.OrderReturnConfirmedEvent;
import com.nhnacademy.shop.order.claim.service.OrderRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@RequiredArgsConstructor
@Component
public class OrderRefundListener {
    private final OrderRefundService orderRefundService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void processAfterReturnConfirmed(OrderReturnConfirmedEvent event) {
        orderRefundService.refund(new OrderRefundCommand(event.orderClaimId()));
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void processAfterOrderCancelled(OrderCancelledEvent event) {
        orderRefundService.refund(new OrderRefundCommand(event.orderClaimId()));
    }
}
