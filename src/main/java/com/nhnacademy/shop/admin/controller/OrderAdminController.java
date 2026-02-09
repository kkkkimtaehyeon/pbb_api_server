package com.nhnacademy.shop.admin.controller;

import com.nhnacademy.shop.admin.dto.OrderManagementResponse;
import com.nhnacademy.shop.admin.service.OrderAdminService;
import com.nhnacademy.shop.common.enums.OrderClaimType;
import com.nhnacademy.shop.common.enums.OrderStatus;
import com.nhnacademy.shop.common.response.ApiResponse;
import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.delivery.dto.DeliveryRegistrationRequest;
import com.nhnacademy.shop.delivery.service.DeliveryService;
import com.nhnacademy.shop.order.claim.dto.OrderClaimListResponse;
import com.nhnacademy.shop.order.claim.service.OrderReturnService;
import com.nhnacademy.shop.order.v2.dto.OrderDetailResponse;
import com.nhnacademy.shop.order.v2.dto.OrderSimpleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
@RestController
public class OrderAdminController {
    private final OrderAdminService orderAdminService;
    private final OrderReturnService orderReturnService;
    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<OrderManagementResponse> getAllOrders(Pageable pageable,
                                                                @RequestParam(name = "status", required = false) OrderStatus status) {
        Page<OrderSimpleResponse> orders = orderAdminService.getAllOrders(pageable, status);
        OrderStatus[] orderStatuses = OrderStatus.values();
        return ResponseEntity.ok(new OrderManagementResponse(orderStatuses, orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable String id) {
        OrderDetailResponse order = orderAdminService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/claims")
    public ResponseEntity<ApiResponse<Page<OrderClaimListResponse>>> getOrderClaims(@RequestParam(required = false)
                                                                                    OrderClaimType claimType,
                                                                                    @PageableDefault(
                                                                                            page = 0,
                                                                                            size = 20,
                                                                                            sort = "createdAt",
                                                                                            direction = Sort.Direction.DESC
                                                                                    ) Pageable pageable) {
        Page<OrderClaimListResponse> response = orderAdminService.getOrderClaims(claimType, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/claims/{orderClaimId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmClaim(@AuthenticationPrincipal MemberDetail memberDetail,
                                                          @PathVariable Long orderClaimId) {
        Long adminMemberId = memberDetail.getMemberId();
        orderReturnService.confirmReturn(adminMemberId, orderClaimId);
        return ResponseEntity.ok(ApiResponse.success(null));

    }

    @PostMapping("/{orderId}/items/{orderItemId}/deliveries")
    public ResponseEntity<Long> registerOrderItemDelivery(@PathVariable String orderId,
                                                          @PathVariable Long orderItemId,
                                                          @AuthenticationPrincipal MemberDetail memberDetail,
                                                          @Valid @RequestBody DeliveryRegistrationRequest request) {
        Long memberId = memberDetail.getMemberId();
        Long id = deliveryService.registerOrderItemDelivery(orderItemId, request);
        return ResponseEntity.ok(id);
    }
}
