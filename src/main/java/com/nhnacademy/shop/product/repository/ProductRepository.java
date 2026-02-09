package com.nhnacademy.shop.product.repository;

import com.nhnacademy.shop.common.enums.ProductType;
import com.nhnacademy.shop.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdWithPessimisticLock(@Param("productId") Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id in :productIds")
    List<Product> findAllByIdWithPessimisticLock(@Param("productIds") Iterable<Long> productIds);

    @Query("SELECT p FROM Product p WHERE p.type = :productType")
    Page<Product> findAll(Pageable pageable, @Param("productType") ProductType productType);

}
