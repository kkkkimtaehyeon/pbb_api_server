package com.nhnacademy.shop.admin.dto;

import com.nhnacademy.shop.common.enums.ProductStatus;
import com.nhnacademy.shop.common.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String productName;
    private String imageUrl;
    private BigDecimal priceSales;
    private Integer stock;
    private ProductStatus status;
    private ProductType type;
    private Long categoryId;
}
