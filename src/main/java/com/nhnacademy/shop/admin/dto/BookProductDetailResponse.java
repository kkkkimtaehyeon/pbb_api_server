package com.nhnacademy.shop.admin.dto;

import com.nhnacademy.shop.book.v2.entity.Book;
import com.nhnacademy.shop.product.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public final class BookProductDetailResponse extends ProductDetailResponse{
    String title;
    BigDecimal priceStandard;
    String isbn13;
    LocalDate publishDate;
    List<Long> authorIds;
    Long publisherId;
    String description;

    public static BookProductDetailResponse from(Product product, Book book) {
        BookProductDetailResponse res = new BookProductDetailResponse();

        // 부모 필드
        res.fillProduct(product);

        // book 전용 필드
        res.title = book.getTitle();
        res.priceStandard = book.getPriceStandard();
        res.isbn13 = book.getIsbn13();
        res.publishDate = book.getPublishDate();
        res.description = book.getSummary();
        res.publisherId = book.getPublisher().getId();
        res.authorIds = book.getAuthors().stream()
                .map(ba -> ba.getAuthor().getId())
                .toList();
        return res;
    }
}
