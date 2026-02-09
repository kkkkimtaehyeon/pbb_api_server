package com.nhnacademy.shop.book.v2.entity;

import com.nhnacademy.shop.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@SuperBuilder
public class Book extends Product {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal priceStandard;

    @Column(nullable = false, unique = true)
    private String isbn13;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<BookAuthor> authors = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", referencedColumnName = "id")
    private Publisher publisher;

    public void addAuthor(Author author) {
        BookAuthor bookAuthor = new BookAuthor(this, author);
        if (authors == null) {
            authors = new ArrayList<>();
        }
        authors.add(bookAuthor);
    }
}
