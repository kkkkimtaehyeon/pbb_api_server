package com.nhnacademy.shop.admin.dto;

import com.nhnacademy.shop.common.enums.ProductStatus;
import com.nhnacademy.shop.common.enums.ProductType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public abstract sealed class ProductRegistrationRequest permits
        BookProductRegistrationRequest
//        MusicProductRegistrationRequest
{
    private ProductType type;
    private String name;
    private BigDecimal priceSales;
    private Integer stock;
    private String imageUrl;
    private Long categoryId;
    private ProductStatus status;
}

