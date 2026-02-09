package com.nhnacademy.shop.book.v2.service;

import com.nhnacademy.shop.book.v2.entity.Author;
import com.nhnacademy.shop.book.v2.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public Author getOrCreateAuthor(String authorName) {
        return authorRepository.findByName(authorName)
                .orElseGet(() -> authorRepository.save(new Author(authorName)));
    }

    public List<Author> getAuthors(List<Long> ids) {
        return null;
    }
}
