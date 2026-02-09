package com.nhnacademy.shop.product.service;

import com.nhnacademy.shop.order.v2.dto.OrderItemCreationRequest;
import com.nhnacademy.shop.product.entity.Product;
import com.nhnacademy.shop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public void validateOrderableProducts(List<OrderItemCreationRequest> requests) {
        List<Long> productIds = requests.stream().map(OrderItemCreationRequest::productId).toList();
        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = new HashMap<>();
        for (Product product: products) {
            productMap.put(product.getId(), product);
        }
        for (OrderItemCreationRequest request: requests) {
            Product product = productMap.get(request.productId());
            if (product == null) {
                throw new IllegalArgumentException("존재하지 않는 상품입니다.");
            }
            if (product.getPriceSales().compareTo(request.price()) != 0) {
                throw new IllegalArgumentException("상품 금액이 일치하지 않습니다.");
            }
            if (product.getStock() < request.quantity()) {
                throw new IllegalArgumentException("해당상품의 재고가 부족합니다.");
            }
        }
    }
}
