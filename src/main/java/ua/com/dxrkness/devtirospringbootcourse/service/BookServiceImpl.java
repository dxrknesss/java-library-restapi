package ua.com.dxrkness.devtirospringbootcourse.service;

import org.springframework.stereotype.Service;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import ua.com.dxrkness.devtirospringbootcourse.repository.BookRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(String bookIsbn, Book newBook) {
        newBook.setIsbn(bookIsbn);
        return bookRepository.save(newBook);
    }

    @Override
    public List<Book> findAll() {
        return StreamSupport.stream(
                bookRepository.findAll().spliterator(), false
        ).toList();
    }
}
