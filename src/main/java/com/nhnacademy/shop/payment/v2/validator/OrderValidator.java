package com.nhnacademy.shop.payment.v2.validator;

import com.nhnacademy.shop.deliveryAddress.service.DeliveryAddressService;
import com.nhnacademy.shop.member.v2.service.MemberService;
import com.nhnacademy.shop.order.v2.dto.OrderCreationRequest;
import com.nhnacademy.shop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderValidator {
    private final MemberService memberService;
    private final DeliveryAddressService deliveryAddressService;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public void validateCreateOrder(Long memberId, OrderCreationRequest request) {
        // 회원 검증
        memberService.validateMember(memberId);
        // 주소 검증
        deliveryAddressService.validateDeliveryAddress(memberId, request.getDeliveryAddressId());
        // 상품 검증
        productService.validateOrderableProducts(request.getItems());
    }


}
