package com.nhnacademy.shop.cart.controller;

import com.nhnacademy.shop.cart.dto.CartAdditionRequest;
import com.nhnacademy.shop.cart.dto.CartItemRemoveRequest;
import com.nhnacademy.shop.cart.dto.CartResponse;
import com.nhnacademy.shop.cart.service.CartService;
import com.nhnacademy.shop.common.security.MemberDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/carts")
@RestController
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = memberDetail.getMemberId();
        CartResponse cart = cartService.getCart(memberId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<?> addCartItem(@AuthenticationPrincipal MemberDetail memberDetail,
                                         @RequestBody CartAdditionRequest request) {
        Long memberId = memberDetail.getMemberId();
        cartService.addItem(memberId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> removeCartItem(@AuthenticationPrincipal MemberDetail memberDetail,
                                            @RequestBody CartItemRemoveRequest request) {
        Long memberId = memberDetail.getMemberId();
        cartService.removeItem(memberId, request);
        return ResponseEntity.ok().build();
    }
}

