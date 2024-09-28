package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookDaoJdbcTemplateIntegrationTest {
    private final AuthorDao authorDao;
    private final BookDaoJdbcTemplate bookDao;

    @Autowired
    public BookDaoJdbcTemplateIntegrationTest(BookDaoJdbcTemplate bookDao, AuthorDao authorDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
    }

    @BeforeEach
    public void setup() {
        for (var author : TestDataUtil.createTestAuthorsList()) {
            authorDao.create(author);
        }
    }

    @Test
    public void book_canBeCreated_andThen_retrieved() {
        var book = TestDataUtil.createTestBookA();

        bookDao.create(book);
        var optionalBook = bookDao.findOne(book.getIsbn());

        Assertions.assertTrue(optionalBook.isPresent());
        Assertions.assertEquals(optionalBook.get(), book);
    }

    @Test
    public void manyBooks_canBeCreated_andThen_retrieved() {
        var dummyBooks = TestDataUtil.createTestBooksList();
        for (var book : dummyBooks) {
            bookDao.create(book);
        }

        var booksFromDb = bookDao.findAll();

        Assertions.assertEquals(dummyBooks.size(), booksFromDb.size());
        Assertions.assertIterableEquals(dummyBooks, booksFromDb);
    }
}
