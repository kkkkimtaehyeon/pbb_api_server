package com.nhnacademy.shop.order.claim.validator;

import com.nhnacademy.shop.member.v2.service.MemberService;
import com.nhnacademy.shop.order.v2.dto.OrderProductReturnRequest;
import com.nhnacademy.shop.order.v2.entity.OrderClaim;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.order.claim.repository.OrderClaimRepository;
import com.nhnacademy.shop.order.v2.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderReturnValidator {
    private final MemberService memberService;
    private final OrderItemRepository orderItemRepository;
    private final OrderClaimRepository orderClaimRepository;

    @Transactional(readOnly = true)
    public void validateReturnRequest(Long memberId, String orderId, Long orderItemId, OrderProductReturnRequest request) {
        // 회원 검증
        memberService.validateMember(memberId);
        // 주문상품 검증
        OrderItem item = orderItemRepository.findByIdAndOrder_IdAndOrder_Member_Id(orderItemId, orderId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("주문상품이 존재하지 않습니다."));
        if (!(item.getQuantity() >= request.getReturnQuantity())) {
            throw new IllegalArgumentException("반품요청 수량이 주문상품의 수량보다 많습니다.");
        }
        item.isReturnable();
        // TODO: 결제정보 검증
    }

    @Transactional(readOnly = true)
    public void validateReturnConfirm(Long memberId, Long orderClaimId) {
        // 회원 검증
        memberService.validateAdmin(memberId);
        // 상태 검증
        OrderClaim returnClaim = orderClaimRepository.findById(orderClaimId)
                .orElseThrow(() -> new IllegalArgumentException("취소/반품요청이 존재하지 않습니다."));
        if (!returnClaim.isReturnConfirmable()) {
            throw new IllegalArgumentException("반품요청을 승인할 수 없는 상태입니다.");
        }
    }
}
