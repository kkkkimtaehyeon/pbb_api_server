package com.nhnacademy.shop.cart.service;

import com.nhnacademy.shop.cart.dto.*;
import com.nhnacademy.shop.cart.entity.Cart;
import com.nhnacademy.shop.cart.entity.CartItem;
import com.nhnacademy.shop.cart.repository.CartRepository;
import com.nhnacademy.shop.product.entity.Product;
import com.nhnacademy.shop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addItem(Long memberId, CartAdditionRequest request) {
        Cart cart = cartRepository.findById(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));

        for (CartItemAdditionRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품정보가 존재하지 않습니다."));
            cart.addItem(new CartItem(product, itemRequest.getQuantity()));
        }
    }

    @Transactional
    public void removeItem(Long memberId, CartItemRemoveRequest request) {
        Cart cart = cartRepository.findById(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));

        for (Long cartItemId : request.getCartItemIds()) {
            cart.removeItemById(cartItemId);
        }
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long memberId) {
        Cart cart = cartRepository.findById(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));
        // 장바구니 상품을 dto로 변환
        List<CartItemResponse> cartItemResponses = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            cartItemResponses.add(
                    new CartItemResponse(
                            item.getId(),
                            product.getId(),
                            product.getImageUrl(),
                            product.getName(),
                            product.getPriceSales(),
                            item.getQuantity()
                    )
            );
        }
        return new CartResponse(cartItemResponses);
    }
}
