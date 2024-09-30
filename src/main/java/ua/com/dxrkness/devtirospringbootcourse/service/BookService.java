package ua.com.dxrkness.devtirospringbootcourse.service;

import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book save(String bookIsbn, Book newBook);
    List<Book> findAll();
    Optional<Book> findByIsbn(String bookIsbn);
    boolean doesExist(String bookIsbn);
}
