package ua.com.dxrkness.devtirospringbootcourse.dao;

import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

import java.util.Optional;

public interface BookDao {
    void create(Book book);
    Optional<Book> findOne(String isbn);
}
