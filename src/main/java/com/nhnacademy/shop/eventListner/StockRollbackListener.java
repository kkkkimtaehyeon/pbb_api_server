package com.nhnacademy.shop.eventListner;

import com.nhnacademy.shop.order.claim.dto.OrderCancelledEvent;
import com.nhnacademy.shop.order.claim.dto.OrderReturnConfirmedEvent;
import com.nhnacademy.shop.stock.StockService;
import com.nhnacademy.shop.stock.dto.StockRollbackCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@RequiredArgsConstructor
@Component
public class StockRollbackListener {
    private final StockService stockService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void processAfterReturnConfirmed(OrderReturnConfirmedEvent event) {
        stockService.rollbackStock(new StockRollbackCommand(event.orderClaimId(), event.productId(), event.rollbackQuantity()));
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void processAfterOrderCancelled(OrderCancelledEvent event) {
        stockService.rollbackStock(new StockRollbackCommand(event.orderClaimId(), event.productId(), event.rollbackQuantity()));
    }
}
