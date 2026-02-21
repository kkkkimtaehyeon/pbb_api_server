package com.nhnacademy.shop.order.v2.controller;

import com.nhnacademy.shop.common.response.ApiResponse;
import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.order.v2.dto.OrderCreationRequest;
import com.nhnacademy.shop.order.v2.dto.OrderCreationResponse;
import com.nhnacademy.shop.order.v2.dto.OrderDetailResponse;
import com.nhnacademy.shop.order.v2.dto.OrderListResponse;
import com.nhnacademy.shop.order.v2.service.OrderFacade;
import com.nhnacademy.shop.order.v2.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
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

    // @PostMapping
    // public ResponseEntity<OrderCreationResponse>
    // createPendingOrder(@AuthenticationPrincipal MemberDetail memberDetail,
    // @Valid @RequestBody OrderCreationRequest request) {
    // Long memberId = memberDetail.getMemberId();
    // OrderCreationResponse response = orderService.createPendingOrder(memberId,
    // request);
    // return ResponseEntity.ok(response);
    // }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createPendingOrder(
            @AuthenticationPrincipal MemberDetail memberDetail,
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

    // ================== DB 기반 전략 엔드포인트 ==================

    @Operation(summary = "주문 생성 - DB 단순 재고차감")
    @PostMapping("/db/simple")
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createOrderDbSimple(
            @Valid @RequestBody OrderCreationRequest request) {
        return createOrderWithStrategy(request, "DB_SIMPLE");
    }

    @Operation(summary = "주문 생성 - DB 낙관적락 재고차감")
    @PostMapping("/db/optimistic")
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createOrderDbOptimistic(
            @Valid @RequestBody OrderCreationRequest request) {
        return createOrderWithStrategy(request, "DB_OPTIMISTIC");
    }

    @Operation(summary = "주문 생성 - DB 비관적락 재고차감")
    @PostMapping("/db/pessimistic")
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createOrderDbPessimistic(
            @Valid @RequestBody OrderCreationRequest request) {
        return createOrderWithStrategy(request, "DB_PESSIMISTIC");
    }

    // ================== Redis 기반 전략 엔드포인트 ==================

    @Operation(summary = "주문 생성 - Redis 단순 재고차감 (GET/SET)")
    @PostMapping("/redis/simple")
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createOrderRedisSimple(
            @Valid @RequestBody OrderCreationRequest request) {
        return createOrderWithStrategy(request, "REDIS_SIMPLE");
    }

    @Operation(summary = "주문 생성 - Redis 원자연산 재고차감 (DECRBY)")
    @PostMapping("/redis/atomic")
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createOrderRedisAtomic(
            @Valid @RequestBody OrderCreationRequest request) {
        return createOrderWithStrategy(request, "REDIS_ATOMIC");
    }

    @Operation(summary = "주문 생성 - Redis 트랜잭션 재고차감 (MULTI/EXEC)")
    @PostMapping("/redis/transaction")
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createOrderRedisTransaction(
            @Valid @RequestBody OrderCreationRequest request) {
        return createOrderWithStrategy(request, "REDIS_TRANSACTION");
    }

    @Operation(summary = "주문 생성 - Redis Lua Script 재고차감")
    @PostMapping("/redis/lua")
    public ResponseEntity<ApiResponse<OrderCreationResponse>> createOrderRedisLua(
            @Valid @RequestBody OrderCreationRequest request) {
        return createOrderWithStrategy(request, "REDIS_LUA");
    }

    private ResponseEntity<ApiResponse<OrderCreationResponse>> createOrderWithStrategy(
            OrderCreationRequest request,
            String strategyType) {
//        Long memberId = memberDetail.getMemberId();
        Long memberId = 1L;
        OrderCreationResponse response = orderFacade.createOrderWithStrategy(memberId, request, strategyType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

