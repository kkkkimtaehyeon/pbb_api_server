package com.nhnacademy.shop.deliveryAddress.controller;

import com.nhnacademy.shop.common.response.ApiResponse;
import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.deliveryAddress.dto.DeliveryAddressRequest;
import com.nhnacademy.shop.deliveryAddress.dto.DeliveryAddressResponse;
import com.nhnacademy.shop.deliveryAddress.service.DeliveryAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/delivery-address")
@RestController
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createDeliveryAddress(@AuthenticationPrincipal MemberDetail memberDetail,
                                                             @RequestBody DeliveryAddressRequest request) {
        Long memberId = memberDetail.getMemberId();;
        deliveryAddressService.createDeliveryAddress(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{deliveryAddressId}")
    public ResponseEntity<DeliveryAddressResponse> updateDeliveryAddress(@AuthenticationPrincipal MemberDetail memberDetail,
                                                                         @PathVariable Long deliveryAddressId,
                                                                         @RequestBody DeliveryAddressRequest request) {
        Long memberId = memberDetail.getMemberId();;
        DeliveryAddressResponse response = deliveryAddressService.updateDeliveryAddress(memberId, deliveryAddressId,
                request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{deliveryAddressId}")
    public ResponseEntity<Void> deleteDeliveryAddress(@AuthenticationPrincipal MemberDetail memberDetail,
                                                      @PathVariable Long deliveryAddressId) {
        Long memberId = memberDetail.getMemberId();;
        deliveryAddressService.deleteDeliveryAddress(memberId, deliveryAddressId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DeliveryAddressResponse>> getDeliveryAddresses(@AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = memberDetail.getMemberId();;
        return ResponseEntity.ok(deliveryAddressService.getDeliveryAddresses(memberId));
    }

    @GetMapping("/default")
    public ResponseEntity<DeliveryAddressResponse> getDefaultDeliveryAddress(@AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = memberDetail.getMemberId();;
        return ResponseEntity.ok(deliveryAddressService.getDefaultDeliveryAddress(memberId));
    }

    @GetMapping("/{deliveryAddressId}")
    public ResponseEntity<DeliveryAddressResponse> getDeliveryAddress(@AuthenticationPrincipal MemberDetail memberDetail,
                                                                      @PathVariable Long deliveryAddressId) {
        Long memberId = memberDetail.getMemberId();;
        return ResponseEntity.ok(deliveryAddressService.getDeliveryAddress(memberId, deliveryAddressId));
    }
}
