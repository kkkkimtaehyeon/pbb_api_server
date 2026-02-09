package com.nhnacademy.shop.book.v2.controller;

import com.nhnacademy.shop.book.v2.dto.BookDetailResponse;
import com.nhnacademy.shop.book.v2.dto.BookSimpleResponse;
import com.nhnacademy.shop.book.v2.service.BookImportService;
import com.nhnacademy.shop.book.v2.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/books")
public class BookController {
    private final BookImportService bookImportService;
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<BookSimpleResponse>> getBooks(Pageable pageable) {
        Page<BookSimpleResponse> books = bookService.getBooks(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getBook(@PathVariable Long id) {
        BookDetailResponse book = bookService.getBook(id);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importBooks(@RequestParam int size,
                                         @RequestParam String queryType,
                                         @RequestParam String searchTarget) {
        bookImportService.importBooks(size, queryType, searchTarget);
        return ResponseEntity.ok().build();
    }
}
