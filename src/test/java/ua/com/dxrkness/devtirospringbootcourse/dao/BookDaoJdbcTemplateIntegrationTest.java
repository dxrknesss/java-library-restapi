package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
        if (authorDao.findOne(1L).isEmpty())  {
            authorDao.create(TestDataUtil.createTestAuthor());
        }
    }

    @Test
    public void book_canBeCreated_andThen_retrieved() {
        var book = TestDataUtil.createTestBook();

        bookDao.create(book);
        var optionalBook = bookDao.findOne(book.getIsbn());

        Assertions.assertTrue(optionalBook.isPresent());
        Assertions.assertEquals(optionalBook.get(), book);
    }
}
