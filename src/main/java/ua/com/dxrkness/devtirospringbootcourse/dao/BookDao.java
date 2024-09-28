package ua.com.dxrkness.devtirospringbootcourse.dao;

import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    void create(Book book);
    Optional<Book> findOne(String isbn);
    List<Book> findAll();
    void update(String bookIsbn, Book newBook);
    void delete(String bookIsbn);
}
