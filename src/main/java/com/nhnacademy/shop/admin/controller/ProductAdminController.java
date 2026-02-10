package com.nhnacademy.shop.admin.controller;

import com.nhnacademy.shop.admin.dto.ProductDetailResponse;
import com.nhnacademy.shop.admin.dto.ProductManagementResponse;
import com.nhnacademy.shop.admin.dto.ProductUpdateRequest;
import com.nhnacademy.shop.admin.service.ProductAdminService;
import com.nhnacademy.shop.admin.dto.ProductRegistrationRequest;
import com.nhnacademy.shop.common.enums.ProductType;
import com.nhnacademy.shop.product.dto.ProductSimpleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/admin/products")
@RestController
public class ProductAdminController {
    private final ProductAdminService productAdminService;

    @GetMapping
    public ResponseEntity<ProductManagementResponse> getAllProducts(Pageable pageable
            , @RequestParam(value = "productType", required = false) ProductType productType
    ) {
        Page<ProductSimpleResponse> products = productAdminService.getAllProducts(pageable, productType);
        ProductType[] productTypes = ProductType.values();
        return ResponseEntity.ok(new ProductManagementResponse(productTypes, products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable Long id) {
        ProductDetailResponse product = productAdminService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Long> registerProduct(@Valid @RequestBody ProductRegistrationRequest request) {
        Long productId = productAdminService.registerProduct(request);
        return ResponseEntity.ok(productId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable Long id,
                                              @Valid @RequestBody ProductUpdateRequest request) {
        productAdminService.updateProductStatus(id, request);
        return ResponseEntity.ok().build();
    }


}
