package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

import java.util.Collection;
import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BookRepositoryIntegrationTest {
    private final BookRepository bookRepository;

    private Book singleBook;
    private List<Book> bookList;

    @Autowired
    public BookRepositoryIntegrationTest(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @BeforeEach
    public void setup() {
        singleBook = TestDataUtil.createTestBookA();
        bookList = TestDataUtil.createTestBooksList();
    }

    @Test
    public void book_canBeCreated_andThen_retrieved() {
        bookRepository.save(singleBook);

        var optionalBook = bookRepository.findById(singleBook.getIsbn());

        Assertions.assertTrue(optionalBook.isPresent());
        Assertions.assertEquals(singleBook, optionalBook.get());
    }

    @Test
    public void manyBooks_canBeCreated_andThen_retrieved() {
        bookRepository.saveAll(bookList);

        var booksFromDb = (Collection<Book>) bookRepository.findAll();

        Assertions.assertEquals(bookList.size(), booksFromDb.size());
        Assertions.assertIterableEquals(bookList, booksFromDb);
    }

    @Test
    public void book_thatExists_updatesCorrectly() {
        bookRepository.save(singleBook);

        singleBook.setTitle("Updated");
        bookRepository.save(singleBook);
        var bookFromDb = bookRepository.findById(singleBook.getIsbn());

        Assertions.assertTrue(bookFromDb.isPresent());
        Assertions.assertEquals(singleBook, bookFromDb.get());
    }

    @Test
    public void book_thatExists_deletesCorrectly() {
        bookRepository.save(singleBook);

        bookRepository.delete(singleBook);
        var bookFromDb = bookRepository.findById(singleBook.getIsbn());

        Assertions.assertTrue(bookFromDb.isEmpty());
    }
}
