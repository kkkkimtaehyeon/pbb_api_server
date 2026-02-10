package com.nhnacademy.shop.delivery.controller;

import com.nhnacademy.shop.delivery.dto.DeliveryDetailResponse;
import com.nhnacademy.shop.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/deliveries")
@RestController
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDetailResponse> getDelivery(@PathVariable Long id) {
        DeliveryDetailResponse delivery = deliveryService.getDelivery(id);
        return ResponseEntity.ok(delivery);
    }
}
