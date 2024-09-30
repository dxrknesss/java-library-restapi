package ua.com.dxrkness.devtirospringbootcourse.service;

import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import java.util.List;

public interface BookService {
    Book save(String bookIsbn, Book newBook);
    List<Book> findAll();
}
