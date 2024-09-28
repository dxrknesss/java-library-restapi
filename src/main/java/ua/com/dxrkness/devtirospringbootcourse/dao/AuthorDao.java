package ua.com.dxrkness.devtirospringbootcourse.dao;

import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

import java.util.Optional;

public interface AuthorDao {
    void create(Author author);
    Optional<Author> findOne(Long id);
}
