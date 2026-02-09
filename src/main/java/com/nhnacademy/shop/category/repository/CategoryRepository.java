package com.nhnacademy.shop.category.repository;

import com.nhnacademy.shop.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndParent(String name, Category parent);

    @Query("select c from Category c left join fetch c.parent")
    List<Category> findAllCategories();
}
