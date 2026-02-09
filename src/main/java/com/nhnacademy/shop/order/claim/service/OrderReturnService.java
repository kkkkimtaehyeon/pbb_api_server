package com.nhnacademy.shop.order.claim.service;

import com.nhnacademy.shop.order.claim.dto.ReturnRequestProcessCommand;
import com.nhnacademy.shop.order.v2.dto.OrderProductReturnRequest;
import com.nhnacademy.shop.order.claim.validator.OrderReturnValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderReturnService {
    private final OrderReturnValidator orderReturnValidator;
    private final OrderReturnProcessor orderReturnProcessor;

    @Transactional
    public void requestReturn(Long memberId, String orderId, Long orderItemId, OrderProductReturnRequest request) {
        // 반품요청 조건검증
        orderReturnValidator.validateReturnRequest(memberId, orderId, orderItemId, request);
        // 반품요청 정보저장
        orderReturnProcessor.processReturnRequest(new ReturnRequestProcessCommand(memberId, orderItemId, request.getReturnReason(),request.getReturnQuantity()));
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    public void confirmReturn(Long memberId, Long orderClaimId) {
        // 반품승인 조건검증
        orderReturnValidator.validateReturnConfirm(memberId, orderClaimId);
        // 반품승인 처리
        orderReturnProcessor.processReturnConfirm(orderClaimId);
    }


}
