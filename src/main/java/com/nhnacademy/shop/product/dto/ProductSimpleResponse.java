package com.nhnacademy.shop.product.dto;

import com.nhnacademy.shop.common.enums.ProductStatus;
import com.nhnacademy.shop.common.enums.ProductStatusResponse;
import com.nhnacademy.shop.common.enums.ProductType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSimpleResponse {
    private Long id;
    private String imageUrl;
    private String name;
    private String category;
    private BigDecimal priceSales;
    private Integer stock;
    private ProductStatusResponse status;
    private ProductType type;

    @Builder
    public ProductSimpleResponse(Long id, String imageUrl, String name, String category, BigDecimal priceSales, Integer stock, ProductStatus status, ProductType type) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.category = category;
        this.priceSales = priceSales;
        this.stock = stock;
        this.status = new ProductStatusResponse(status);
        this.type = type;
    }
}

