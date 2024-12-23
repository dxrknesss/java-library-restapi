package ua.com.dxrkness.devtirospringbootcourse.service;

import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Author save(Author newAuthor);
    List<Author> saveAll(List<Author> newAuthors);
    List<Author> findAll();
    Optional<Author> findById(Long authorId);
    boolean doesExist(Long authorId);
    Author partialUpdate(Long authorId, Author partiallyUpdatedAuthor);
    void delete(Long authorId);
}
