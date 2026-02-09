package com.nhnacademy.shop.stock;


import com.nhnacademy.shop.order.claim.repository.OrderClaimRepository;
import com.nhnacademy.shop.order.v2.entity.OrderClaim;
import com.nhnacademy.shop.product.entity.Product;
import com.nhnacademy.shop.product.repository.ProductRepository;
import com.nhnacademy.shop.stock.dto.StockRollbackCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DbStockService implements StockService {
    private final ProductRepository productRepository;
    private final OrderClaimRepository orderClaimRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollbackStock(StockRollbackCommand command) {
        OrderClaim orderClaim= orderClaimRepository.findById(command.orderClaimId())
                .orElseThrow();
        // 재고 복구
        Product product = productRepository.findByIdWithPessimisticLock(command.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품정보가 존재하지 않습니다."));
        product.addStock(command.rollbackQuantity());
        // 재고복구완료상태 기록
        orderClaim.stockRolledBack();
    }
}
