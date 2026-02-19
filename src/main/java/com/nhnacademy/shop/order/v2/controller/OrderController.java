package com.nhnacademy.shop.order.v2.controller;

import com.nhnacademy.shop.common.response.ApiResponse;
import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.order.v2.dto.OrderCreationRequest;
import com.nhnacademy.shop.order.v2.dto.OrderCreationResponse;
import com.nhnacademy.shop.order.v2.dto.OrderDetailResponse;
import com.nhnacademy.shop.order.v2.dto.OrderListResponse;
import com.nhnacademy.shop.order.v2.service.OrderFacade;
import com.nhnacademy.shop.order.v2.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v2/orders")
@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderService orderService;
    private final OrderFacade orderFacade;

//    @PostMapping
//    public ResponseEntity<OrderCreationResponse> createPendingOrder(@AuthenticationPrincipal MemberDetail memberDetail,
//            @Valid @RequestBody OrderCreationRequest request) {
//        Long memberId = memberDetail.getMemberId();
//        OrderCreationResponse response = orderService.createPendingOrder(memberId, request);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createPendingOrder(@AuthenticationPrincipal MemberDetail memberDetail,
                                                                                @Valid @RequestBody OrderCreationRequest request) {
        Long memberId = memberDetail.getMemberId();
        OrderCreationResponse response = orderFacade.createOrder(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<Page<OrderListResponse>> getOrders(@AuthenticationPrincipal MemberDetail memberDetail,
                                                             Pageable pageable) {
        Long memberId = memberDetail.getMemberId();
        Page<OrderListResponse> response = orderService.getOrders(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable("id") String orderId) {
        OrderDetailResponse response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/items/{orderItemId}/confirm")
    public ResponseEntity<?> returnProduct(@PathVariable String orderId,
                                           @PathVariable Long orderItemId,
                                           @AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = memberDetail.getMemberId();
        orderService.confirmPurchase(memberId, orderId, orderItemId);
        return ResponseEntity.ok().build();
    }

}
