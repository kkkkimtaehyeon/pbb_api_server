package com.nhnacademy.shop.admin.dto;

import com.nhnacademy.shop.common.enums.ProductType;
import com.nhnacademy.shop.product.dto.ProductSimpleResponse;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

@Data
public class ProductManagementResponse {
    List<ProductTypeResponse> productTypes;
    Page<ProductSimpleResponse> products;

    public ProductManagementResponse(ProductType[] productTypes, Page<ProductSimpleResponse> products) {
        this.productTypes = Arrays.stream(productTypes).map(ProductTypeResponse::new).toList();
        this.products = products;
    }
}

@Getter
class ProductTypeResponse {
    private final String status;
    private final String displayName;

    public ProductTypeResponse(ProductType type) {
        this.status = type.getStatus();
        this.displayName = type.getDisplayName();
    }

    public static ProductTypeResponse of(ProductType productType) {
        return new ProductTypeResponse(productType);
    }
}
