package com.nhnacademy.shop.category.service;

import com.nhnacademy.shop.category.dto.CategoryResponse;
import com.nhnacademy.shop.category.entity.Category;
import com.nhnacademy.shop.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category getOrCreateCategory(String categoryPath) {
        String[] names = categoryPath.split(">");
        Category parent = null;
        int depth = 1;

        for (String name : names) {
            String trimmedName = name.trim();

            // 람다식에서 사용하기 위해 "변하지 않는 변수"로 재할당
            Category finalParent = parent;
            int currentDepth = depth; // ⭐ 이 부분이 추가되어야 합니다!

            parent = categoryRepository.findByNameAndParent(trimmedName, finalParent)
                    .orElseGet(() -> categoryRepository.save(
                            Category.builder()
                                    .name(trimmedName)
                                    .depth(currentDepth) // ⭐ depth 대신 currentDepth 사용
                                    .parent(finalParent)
                                    .build()
                    ));
            depth++;
        }

        return parent;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategoriesTree() {
        List<Category> categories = categoryRepository.findAllCategories();

        // 1. id → CategoryResponse 맵
        Map<Long, CategoryResponse> map = new HashMap<>();

        for (Category category : categories) {
            map.put(
                    category.getId(),
                    new CategoryResponse(category.getId(), category.getName())
            );
        }

        // 2. 트리 구성
        List<CategoryResponse> roots = new ArrayList<>();

        for (Category category : categories) {
            CategoryResponse current = map.get(category.getId());

            if (category.getParent() == null) {
                // root
                roots.add(current);
            } else {
                CategoryResponse parent =
                        map.get(category.getParent().getId());
                parent.getChildren().add(current);
            }
        }

        return roots;
    }
}
