package com.nhnacademy.shop.order.claim.service;

import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.member.v2.repository.MemberRepository;
import com.nhnacademy.shop.order.claim.dto.OrderReturnConfirmedEvent;
import com.nhnacademy.shop.order.claim.dto.ReturnRequestProcessCommand;
import com.nhnacademy.shop.order.claim.repository.OrderClaimRepository;
import com.nhnacademy.shop.order.v2.entity.OrderClaim;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.order.v2.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class OrderReturnProcessor {
    private final OrderItemRepository orderItemRepository;
    private final OrderClaimRepository orderClaimRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void processReturnRequest(ReturnRequestProcessCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow();
        OrderItem item = orderItemRepository.findById(command.orderItemId())
                .orElseThrow();
        // 주문상품 상태 "반품요청" 처리
        item.markAsReturnRequested();
        // 택배사에 반송요청 & 반송장번호 발급
        String returnTrackingNumber = "123456712334";
        // 환불 금액 계산
        BigDecimal refundAmount = item.getPrice().multiply(BigDecimal.valueOf(command.returnQuantity()));
        OrderClaim returnClaim = OrderClaim.returns(
                command.returnReason(),
                returnTrackingNumber,
                refundAmount,
                item,
                member
        );
        orderClaimRepository.save(returnClaim);
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @Transactional
    public void processReturnConfirm(Long orderClaimId) {
        OrderClaim returnClaim = orderClaimRepository.findById(orderClaimId)
                .orElseThrow();
        // 관리자: 환불처리중, 사용자: 반품완료로 상태변경
        OrderItem orderItem = returnClaim.getOrderItem();
        orderItem.completeReturn();
        // 반품완료처리 이벤트 발행
        applicationEventPublisher.publishEvent(new OrderReturnConfirmedEvent(
                orderClaimId,
                orderItem.getProduct().getId(),
                orderItem.getQuantity()
        ));
    }
}
