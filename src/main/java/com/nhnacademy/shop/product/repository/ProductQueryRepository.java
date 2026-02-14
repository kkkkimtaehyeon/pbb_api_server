package com.nhnacademy.shop.product.repository;

import com.nhnacademy.shop.book.v2.dto.BookSearchRequest;
import com.nhnacademy.shop.book.v2.dto.BookSimpleResponse;
import com.nhnacademy.shop.book.v2.entity.Book;
import com.nhnacademy.shop.book.v2.entity.QBook;
import com.nhnacademy.shop.category.entity.QCategory;
import com.nhnacademy.shop.product.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ProductQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<BookSimpleResponse> findAllBookProduct(Pageable pageable, BookSearchRequest searchRequest) {
        QBook book = QBook.book;
        QProduct product = QProduct.product;
        QCategory category = QCategory.category;

        List<Long> categoryIds = getCategoryIds(searchRequest.getCategoryId());

        List<Book> content = queryFactory.selectFrom(book)
                .leftJoin(book.publisher).fetchJoin()
                .leftJoin(book.category, category).fetchJoin()
                .where(categoryIdIn(categoryIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(book.count())
                .from(book)
                .where(categoryIdIn(categoryIds))
                .fetchOne();

        List<BookSimpleResponse> responses = content.stream()
                .map(this::convertToBookSimpleResponse)
                .toList();

        return new PageImpl<>(responses, pageable, total != null ? total : 0);
    }

    private BooleanExpression categoryIdIn(List<Long> categoryIds) {
        return !categoryIds.isEmpty() ? QBook.book.category.id.in(categoryIds) : null;
    }

    private List<Long> getCategoryIds(Long rootCategoryId) {
        if (rootCategoryId == null) {
            return Collections.emptyList();
        }

        QCategory category = QCategory.category;
        List<Tuple> results = queryFactory.select(category.id, category.parent.id)
                .from(category)
                .fetch();

        Map<Long, List<Long>> parentToChildren = new HashMap<>();
        for (Tuple t : results) {
            Long id = t.get(category.id);
            Long parentId = t.get(category.parent.id);
            if (parentId != null) {
                parentToChildren.computeIfAbsent(parentId, k -> new ArrayList<>()).add(id);
            }
        }

        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(rootCategoryId);

        Queue<Long> queue = new LinkedList<>();
        queue.add(rootCategoryId);

        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            List<Long> children = parentToChildren.get(currentId);
            if (children != null) {
                categoryIds.addAll(children);
                queue.addAll(children);
            }
        }

        return categoryIds;
    }

    private BookSimpleResponse convertToBookSimpleResponse(Book book) {
        String authorNames = book.getAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName())
                .collect(Collectors.joining(", "));

        return BookSimpleResponse.builder()
                .id(book.getId())
                .imageUrl(book.getImageUrl())
                .title(book.getTitle())
                .authors(authorNames)
                .publisher(book.getPublisher() != null ? book.getPublisher().getName() : null)
                .publishDate(book.getPublishDate().toString())
                .priceStandard(book.getPriceStandard())
                .priceSales(book.getPriceSales())
                .categoryName(book.getCategory().getName())
                .build();
    }

}
