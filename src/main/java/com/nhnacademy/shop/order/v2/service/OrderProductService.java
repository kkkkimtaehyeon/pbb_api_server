package com.nhnacademy.shop.order.v2.service;


import com.nhnacademy.shop.order.v2.repository.OrderRepository;
import com.nhnacademy.shop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OrderProductService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addProducts(String orderId, Map<Long, Integer> productQuantities) {

    }
}
