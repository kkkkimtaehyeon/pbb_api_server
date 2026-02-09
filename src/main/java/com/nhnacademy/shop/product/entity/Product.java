package com.nhnacademy.shop.product.entity;

import com.nhnacademy.shop.admin.dto.ProductUpdateRequest;
import com.nhnacademy.shop.category.entity.Category;
import com.nhnacademy.shop.common.enums.ProductStatus;
import com.nhnacademy.shop.common.enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSales;

    @Column(nullable = false)
    private String imageUrl;

    @Setter
    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType type;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    public void update(ProductUpdateRequest request) {
        this.name = request.getProductName();
        this.status = request.getStatus();
        this.priceSales = request.getPriceSales();
        this.imageUrl = request.getImageUrl();
        this.stock = request.getStock();
        this.type = request.getType();
    }

    public void addStock(int stock) {
        this.stock += stock;
    }
}

