package com.nhnacademy.shop.admin.dto;

import com.nhnacademy.shop.common.enums.ProductStatus;
import com.nhnacademy.shop.common.enums.ProductType;
import com.nhnacademy.shop.product.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public abstract sealed class ProductDetailResponse permits BookProductDetailResponse{
    private ProductType productType;
    private String productName;
    private String categoryName;
    private BigDecimal priceSales;
    private int stock;
    private String imageUrl;
    private ProductStatus productStatus;

    protected void fillProduct(Product product) {
        this.productType = product.getType();
        this.productName = product.getName();
        this.categoryName = product.getCategory().getName();
        this.priceSales = product.getPriceSales();
        this.stock = product.getStock();
        this.imageUrl = product.getImageUrl();
        this.productStatus = product.getStatus();
    }

}
