package com.nhnacademy.shop.cart.repository;

import com.nhnacademy.shop.cart.entity.Cart;
import org.springframework.data.repository.CrudRepository;

public interface CartRedisRepository extends CrudRepository<Cart, Long> {
}
