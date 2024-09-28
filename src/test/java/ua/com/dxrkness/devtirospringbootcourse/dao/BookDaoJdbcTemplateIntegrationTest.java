package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookDaoJdbcTemplateIntegrationTest {
    private final AuthorDao authorDao;
    private final BookDaoJdbcTemplate bookDao;

    private Book singleBook;
    private List<Book> bookList;

    @Autowired
    public BookDaoJdbcTemplateIntegrationTest(BookDaoJdbcTemplate bookDao, AuthorDao authorDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
    }

    @BeforeEach
    public void setup() {
        singleBook = TestDataUtil.createTestBookA();
        bookList = TestDataUtil.createTestBooksList();

        for (var author : TestDataUtil.createTestAuthorsList()) {
            authorDao.create(author);
        }
    }

    @Test
    public void book_canBeCreated_andThen_retrieved() {
        bookDao.create(singleBook);

        var optionalBook = bookDao.findOne(singleBook.getIsbn());

        Assertions.assertTrue(optionalBook.isPresent());
        Assertions.assertEquals(optionalBook.get(), singleBook);
    }

    @Test
    public void manyBooks_canBeCreated_andThen_retrieved() {
        for (var book : bookList) {
            bookDao.create(book);
        }
        var booksFromDb = bookDao.findAll();

        Assertions.assertEquals(bookList.size(), booksFromDb.size());
        Assertions.assertIterableEquals(bookList, booksFromDb);
    }

    @Test
    public void book_thatExists_updatesCorrectly() {
        var bookIsbn = singleBook.getIsbn();

        bookDao.create(singleBook);
        singleBook.setIsbn("000000000000000000");
        singleBook.setTitle("Updated");
        bookDao.update(bookIsbn, singleBook);
        var bookFromDb = bookDao.findOne(singleBook.getIsbn());

        Assertions.assertTrue(bookFromDb.isPresent());
        Assertions.assertEquals(bookFromDb.get(), singleBook);
    }

    @Test
    public void book_thatExists_deletesCorrectly() {
        bookDao.create(singleBook);

        bookDao.delete(singleBook.getIsbn());
        var bookFromDb = bookDao.findOne(singleBook.getIsbn());

        Assertions.assertTrue(bookFromDb.isEmpty());
    }
}
