package com.nhnacademy.shop.book.v2.repository;

import com.nhnacademy.shop.book.v2.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
}
