package com.nhnacademy.shop.order.claim.controller;

import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.order.claim.dto.OrderCancelRequest;
import com.nhnacademy.shop.order.claim.service.OrderCancelService;
import com.nhnacademy.shop.order.claim.service.OrderReturnService;
import com.nhnacademy.shop.order.v2.dto.OrderProductReturnRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v2/orders")
@RequiredArgsConstructor
@RestController
public class OrderClaimController {

    private final OrderCancelService orderCancelService;
    private final OrderReturnService orderReturnService;

    @PostMapping("/{orderId}/items/{orderItemId}/cancel")
    public ResponseEntity<Long> cancelOrder(@PathVariable String orderId,
                                            @PathVariable Long orderItemId,
                                            @AuthenticationPrincipal MemberDetail memberDetail,
                                            @Valid @RequestBody OrderCancelRequest request) {
        Long memberId = memberDetail.getMemberId();
        orderCancelService.cancelOrder(memberId, orderId, orderItemId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/items/{orderItemId}/return")
    public ResponseEntity<?> returnProduct(@PathVariable String orderId,
                                           @PathVariable Long orderItemId,
                                           @AuthenticationPrincipal MemberDetail memberDetail,
                                           @Valid @RequestBody OrderProductReturnRequest request) {
        Long memberId = memberDetail.getMemberId();
        orderReturnService.requestReturn(memberId, orderId, orderItemId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/claims/{orderClaimId}/return/confirm")
    public ResponseEntity<?> confirmReturnRequest(@PathVariable Long orderClaimId,
                                                  @AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = memberDetail.getMemberId();
        orderReturnService.confirmReturn(memberId, orderClaimId);
        return ResponseEntity.ok().build();
    }
}
