package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookRepositoryIntegrationTest {
    private final AuthorRepository authorDao;
    private final BookRepository bookRepository;

    private Book singleBook;
    private List<Book> bookList;

    @Autowired
    public BookRepositoryIntegrationTest(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorDao = authorRepository;
    }

    @BeforeEach
    public void setup() {
        singleBook = TestDataUtil.createTestBookA();
        bookList = TestDataUtil.createTestBooksList();

        authorDao.saveAll(TestDataUtil.createTestAuthorsList());
    }

    @Test
    public void book_canBeCreated_andThen_retrieved() {
        bookRepository.save(singleBook);

        var optionalBook = bookRepository.findById(singleBook.getIsbn());

        Assertions.assertTrue(optionalBook.isPresent());
        Assertions.assertEquals(optionalBook.get(), singleBook);
    }

    /*
    @Test
    public void manyBooks_canBeCreated_andThen_retrieved() {
        bookRepository.saveAll(bookList);

        var booksFromDb = bookRepository.findAll();

        Assertions.assertEquals(bookList.size(), booksFromDb.size());
        Assertions.assertIterableEquals(bookList, booksFromDb);
    }

    @Test
    public void book_thatExists_updatesCorrectly() {
        var bookIsbn = singleBook.getIsbn();

        bookRepository.save(singleBook);
        singleBook.setIsbn("000000000000000000");
        singleBook.setTitle("Updated");
        bookRepository.update(bookIsbn, singleBook);
        var bookFromDb = bookRepository.findById(singleBook.getIsbn());

        Assertions.assertTrue(bookFromDb.isPresent());
        Assertions.assertEquals(bookFromDb.get(), singleBook);
    }

    @Test
    public void book_thatExists_deletesCorrectly() {
        bookRepository.save(singleBook);

        bookRepository.delete(singleBook);
        var bookFromDb = bookRepository.findById(singleBook.getIsbn());

        Assertions.assertTrue(bookFromDb.isEmpty());
    }
     */
}
