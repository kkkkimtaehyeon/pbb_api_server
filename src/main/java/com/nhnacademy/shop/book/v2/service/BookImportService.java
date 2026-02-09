package com.nhnacademy.shop.book.v2.service;

import com.nhnacademy.shop.book.v2.dto.AladinBookImportResponse;
import com.nhnacademy.shop.book.v2.dto.AladinBookResponse;
import com.nhnacademy.shop.book.v2.entity.Author;
import com.nhnacademy.shop.book.v2.entity.Book;
import com.nhnacademy.shop.book.v2.entity.Publisher;
import com.nhnacademy.shop.book.v2.repository.AuthorRepository;
import com.nhnacademy.shop.book.v2.repository.BookRepository;
import com.nhnacademy.shop.book.v2.repository.PublisherRepository;
import com.nhnacademy.shop.common.enums.ProductStatus;
import com.nhnacademy.shop.common.enums.ProductType;
import com.nhnacademy.shop.category.entity.Category;
import com.nhnacademy.shop.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookImportService {
    private final CategoryService categoryService;
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final RestTemplate restTemplate;

    @Value("${aladin.api.ttbkey}")
    private String ALADIN_TTB_KEY;

    private static final String ALADIN_URL = "https://www.aladin.co.kr/ttb/api/ItemList.aspx";


    @Transactional
    public void importBooks(int size, String queryType, String searchTarget) {

        URI uri = UriComponentsBuilder.fromHttpUrl(ALADIN_URL)
                .queryParam("ttbkey", ALADIN_TTB_KEY)
                .queryParam("QueryType", queryType) // 신간 전체
                .queryParam("MaxResults", size)
                .queryParam("start", 1)
                .queryParam("SearchTarget", searchTarget)
                .queryParam("output", "js") // JSON 포맷 필수
                .queryParam("Version", "20131101")
                .build()
                .toUri();
        AladinBookImportResponse response = restTemplate.getForObject(uri, AladinBookImportResponse.class);
        for (AladinBookResponse book : response.getItem()) {
            saveBookProduct(book);
        }
    }

    @Transactional
    public void saveBookProduct(AladinBookResponse aladinBook) {
        // isbn 중복 스킵
        if (bookRepository.existsByIsbn13(aladinBook.getIsbn13())) {
            log.info("이미 존재하는 ISBN, 스킵: {}", aladinBook.getIsbn13());
            return;
        }
        // 출판사
        Publisher publisher = publisherRepository.findByName(aladinBook.getPublisher())
                .orElseGet(() -> publisherRepository.save(new Publisher(aladinBook.getPublisher())));
        // 카테고리
        Category category = categoryService.getOrCreateCategory(aladinBook.getCategoryName());
        Book book = Book.builder()
                // product
                .name(aladinBook.getTitle())
                .priceSales(new BigDecimal(aladinBook.getPriceSales()))
                .stock(100) // 임의 stock
                .imageUrl(aladinBook.getCover())
                .viewCount(0L)
//                .type(ProductType.BOOK)
                .type(ProductType.from(aladinBook.getMallType()))
                .status(ProductStatus.SELLING)
                .category(category)
                // book
                .isbn13(aladinBook.getIsbn13())
                .summary(aladinBook.getDescription())
                .publishDate(LocalDate.parse(aladinBook.getPubDate()))
                .title(aladinBook.getTitle())
                .priceStandard(new BigDecimal(aladinBook.getPriceStandard()))
                .publisher(publisher)
                .build();
        // 작가 추가
        addAuthors(book, aladinBook.getAuthor());
        bookRepository.save(book);
    }

    @Transactional
    public void addAuthors(Book book, String authorString) {
        // 예: "조호건축사사무소, 뜰과숲 (지은이)"

        // 1. 괄호와 그 안의 내용 제거 ("(지은이)" 제거)
        String cleanString = authorString.replaceAll("\\(.*?\\)", "");

        // 2. 쉼표로 분리
        String[] authorNames = cleanString.split(",");

        for (String name : authorNames) {
            String realName = name.trim(); // 공백 제거
            if (realName.isEmpty()) continue;

            // 3. 저자 조회 혹은 저장 (Author)
            Author author = authorRepository.findByName(realName)
                    .orElseGet(() -> authorRepository.save(new Author(realName)));
            book.addAuthor(author);
        }
    }

}