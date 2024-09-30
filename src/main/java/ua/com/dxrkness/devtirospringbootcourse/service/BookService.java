package ua.com.dxrkness.devtirospringbootcourse.service;

import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

public interface BookService {
    Book save(String bookIsbn, Book newBook);
}
