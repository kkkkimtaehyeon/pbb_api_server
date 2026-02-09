package com.nhnacademy.shop.book.v2.repository;

import com.nhnacademy.shop.book.v2.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn13(String isbn);

}
