package com.nhnacademy.shop.book.v2.service;

import com.nhnacademy.shop.book.v2.dto.BookDetailResponse;
import com.nhnacademy.shop.book.v2.dto.BookSimpleResponse;
import com.nhnacademy.shop.book.v2.entity.Book;
import com.nhnacademy.shop.book.v2.entity.BookAuthor;
import com.nhnacademy.shop.book.v2.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public Page<BookSimpleResponse> getBooks(Pageable pageable) {
        Page<Book> bookPages = bookRepository.findAll(pageable);
        return bookPages.map(book ->
                new BookSimpleResponse(
                        book.getId(),
                        book.getImageUrl(),
                        book.getName(),
                        getAuthorNames(book),
                        book.getPublisher().getName(),
                        book.getPublishDate().toString(),
                        book.getPriceStandard(),
                        book.getPriceSales(),
                        book.getCategory().getName()
//                        book.getType().getDisplayName()
                )
        );
    }

    @Transactional(readOnly = true)
    public BookDetailResponse getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("책 정보가 존재하지 않습니다."));
        return BookDetailResponse.builder()
                .id(book.getId())
                .imageUrl(book.getImageUrl())
                .title(book.getTitle())
                .summary(book.getSummary())
                .authors(getAuthorNames(book))
                .publisher(book.getPublisher().getName())
                .publishDate(book.getPublishDate().toString())
                .priceSales(book.getPriceSales())
                .priceStandard(book.getPriceStandard())
                .build();
    }

    @Transactional(readOnly = true)
    public String getAuthorNames(Book book) {
        List<BookAuthor> authors = book.getAuthors();
        String[] authorNames = new String[authors.size()];
        for (int i = 0; i < authors.size(); i++) {
            authorNames[i] = authors.get(i).getAuthor().getName();
        }
        return String.join(", ", authorNames);
    }
}
