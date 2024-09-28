package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class BookDaoJdbcTemplateTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BookDaoJdbcTemplate bookDao;

    @Test
    public void createBook_generatesValidSql() {
        var book = TestDataUtil.createTestBook();

        bookDao.create(book);

        Mockito.verify(jdbcTemplate).update(
                eq("INSERT INTO books (isbn, title, author_id) VALUES (?, ?, ?)"),
                eq(book.getIsbn()), eq(book.getTitle()), eq(book.getAuthor().getId())
        );
    }

    @Test
    public void findOne_generatesValidSql() {
        var bookOptional = bookDao.findOne("123-123");

        Mockito.verify(jdbcTemplate).query(
                eq("SELECT * FROM books WHERE isbn = ? LIMIT 1"),
                ArgumentMatchers.<BookDaoJdbcTemplate.BookRowMapper>any(),
                eq("123-123")
        );
    }
}