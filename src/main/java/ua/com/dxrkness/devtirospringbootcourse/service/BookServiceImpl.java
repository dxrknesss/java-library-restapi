package ua.com.dxrkness.devtirospringbootcourse.service;

import org.springframework.stereotype.Service;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import ua.com.dxrkness.devtirospringbootcourse.repository.BookRepository;

import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<Book> findByIsbn(String bookIsbn) {
        return bookRepository.findById(bookIsbn);
    }

    @Override
    public boolean doesExist(String bookIsbn) {
        return bookRepository.existsById(bookIsbn);
    }

    @Override
    public Book partialUpdate(String isbn, Book partiallyUpdatedBook) {
        partiallyUpdatedBook.setIsbn(isbn);

        return bookRepository.findById(isbn).map(bookFromDb -> {
            Optional.ofNullable(partiallyUpdatedBook.getTitle()).ifPresent(bookFromDb::setTitle);
            Optional.ofNullable(partiallyUpdatedBook.getAuthor()).ifPresent(bookFromDb::setAuthor);

            return bookFromDb;
        }).orElseThrow(() -> new RuntimeException("I'm too tired to write this. it's 3 in the morning and I need to get over with this project as fast as possible. "));
    }
}
