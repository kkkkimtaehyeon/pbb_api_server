package com.nhnacademy.shop.admin.service;

import com.nhnacademy.shop.admin.dto.*;
import com.nhnacademy.shop.book.v2.entity.Author;
import com.nhnacademy.shop.book.v2.entity.Book;
import com.nhnacademy.shop.book.v2.entity.Publisher;
import com.nhnacademy.shop.book.v2.repository.AuthorRepository;
import com.nhnacademy.shop.book.v2.repository.BookRepository;
import com.nhnacademy.shop.book.v2.repository.PublisherRepository;
import com.nhnacademy.shop.book.v2.service.AuthorService;
import com.nhnacademy.shop.category.entity.Category;
import com.nhnacademy.shop.category.repository.CategoryRepository;
import com.nhnacademy.shop.common.enums.ProductType;
import com.nhnacademy.shop.product.dto.ProductSimpleResponse;
import com.nhnacademy.shop.product.entity.Product;
import com.nhnacademy.shop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ProductAdminService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorService authorService;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public Page<ProductSimpleResponse> getAllProducts(Pageable pageable, ProductType productType) {
        Page<Product> products = null;
        if (productType == null) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findAll(pageable, productType);
        }
        return products.map(product ->
                ProductSimpleResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .priceSales(product.getPriceSales())
                        .imageUrl(product.getImageUrl())
                        .category(product.getCategory().getName())
                        .stock(product.getStock())
                        .status(product.getStatus())
                        .type(product.getType())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품정보가 존재하지 않습니다"));
        switch (product.getType()) {
            case BOOK -> {
                Book book = bookRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("도서정보가 존재하지 않습니다."));
                return BookProductDetailResponse.from(product, book);
            }
        }
        return null;
    }

    @Transactional
    public void updateProductStatus(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품정보가 존재하지 않습니다."));
        product.update(request);

        // 카테고리가 업데이트 됐다면 반영
        if (!Objects.equals(product.getCategory().getId(), request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리정보가 존재하지 않습니다."));
            product.setCategory(category);
        }
    }

    @Transactional
    public Long registerProduct(ProductRegistrationRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 정보가 존재하지 않습니다."));

        Product product = switch (request) {
            case BookProductRegistrationRequest req -> {
                // 1. 출판사 조회
                Publisher publisher = publisherRepository.findById(req.getPublisherId())
                        .orElseThrow(() -> new IllegalArgumentException("출판사 정보가 존재하지 않습니다."));

                // 2. 작가들 한꺼번에 조회 (N+1 방지)
                List<Author> authors = authorRepository.findAllById(req.getAuthorIds());
                if (authors.size() != req.getAuthorIds().size()) {
                    throw new IllegalArgumentException("일부 작가 정보가 존재하지 않습니다.");
                }

                // 3. Book 생성 및 연관관계 편의 메서드 활용
                Book book = createBookProduct(req, category, publisher);
                authors.forEach(book::addAuthor); // 내부에서 new BookAuthor(this, author) 처리

                yield book; // switch 식의 결과값 반환
            }
            // case AlbumProductRegistrationRequest req -> { ... yield album; }
            default -> throw new IllegalArgumentException("지원하지 않는 상품 타입입니다.");
        };

        return productRepository.save(product).getId();
    }


    private Book createBookProduct(BookProductRegistrationRequest req, Category category, Publisher publisher) {
        // Book 전용 생성 로직 (Publisher 조회 등)
        return Book.builder()
                // product
                .name(req.getName())
                .priceSales(req.getPriceSales())
                .stock(req.getStock())
                .imageUrl(req.getImageUrl())
                .viewCount(0L)
                .type(ProductType.BOOK)
                .status(req.getStatus())
                .category(category)
                // book
                .isbn13(req.getIsbn13())
                .summary(req.getSummary())
                .publishDate(req.getPublishDate())
                .title(req.getTitle())
                .priceStandard(req.getPriceStandard())
                .publisher(publisher)
                .build();
    }
}
