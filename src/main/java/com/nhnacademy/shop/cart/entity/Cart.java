package com.nhnacademy.shop.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Cart {
    @Id
    private Long id;

    @OneToMany(mappedBy = "cart",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private List<CartItem> items = new ArrayList<>();


    public Cart(Long memberId) {
        this.id = memberId;
    }

    public void addItem(CartItem newCartItem) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }

        // 1. 기존 장바구니 리스트에서 같은 상품(ProductId 기준)이 있는지 찾습니다.
        items.stream()
                .filter(item -> item.getProduct().getId().equals(newCartItem.getProduct().getId()))
                .findFirst()
                .ifPresentOrElse(
                        // 2. 이미 있다면: 기존 아이템의 수량만 증가시킵니다.
                        existingItem -> existingItem.addCount(newCartItem.getQuantity()),

                        // 3. 없다면: 리스트에 새로 추가하고 관계를 맺습니다.
                        () -> {
                            newCartItem.setCart(this);
                            this.items.add(newCartItem);
                        }
                );
    }

    public void removeItemById(Long cartItemId) {
        boolean removed = items.removeIf(item ->
                item.getId().equals(cartItemId)
        );

        if (!removed) {
            throw new IllegalArgumentException("장바구니 상품 정보가 존재하지 않습니다.");
        }
    }
}
