package ua.com.dxrkness.devtirospringbootcourse.service;

import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Author save(Author newAuthor);
    List<Author> findAll();
    Optional<Author> findById(Long authorId);
}
