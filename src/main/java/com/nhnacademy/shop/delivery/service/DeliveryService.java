package com.nhnacademy.shop.delivery.service;

import com.nhnacademy.shop.delivery.dto.DeliveryDetailResponse;
import com.nhnacademy.shop.delivery.dto.DeliveryRegistrationRequest;
import com.nhnacademy.shop.delivery.entity.OrderDelivery;
import com.nhnacademy.shop.delivery.repository.DeliveryRepository;
import com.nhnacademy.shop.order.v2.entity.OrderItem;
import com.nhnacademy.shop.order.v2.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Long registerOrderItemDelivery(Long orderItemId, DeliveryRegistrationRequest request) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow();
        // 상태 검증
        orderItem.isDeliveryRegistrable();

        OrderDelivery orderDelivery = OrderDelivery.builder()
                .company(request.getCompany())
                .trackingNumber(request.getTrackingNumber())
                .orderItem(orderItem)
                .build();
        deliveryRepository.save(orderDelivery);
        // 주문 상태 "발송완료"로 변경
        orderItem.shipped(orderDelivery);
        return orderDelivery.getId();
    }

    @Transactional(readOnly = true)
    public DeliveryDetailResponse getDelivery(Long deliveryId) {
        OrderDelivery orderDelivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송정보가 존재하지 않습니다."));
        return new DeliveryDetailResponse(orderDelivery.getCompany(), orderDelivery.getTrackingNumber());
    }
}
