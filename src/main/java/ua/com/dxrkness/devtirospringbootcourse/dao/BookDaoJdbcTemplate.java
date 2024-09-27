package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

public class BookDaoJdbcTemplate implements BookDao {
    private final JdbcTemplate jdbcTemplate;

    public BookDaoJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Book book) {
        jdbcTemplate.update(
                "INSERT INTO books (isbn, title, author_id) VALUES (?, ?, ?)",
                book.getIsbn(), book.getTitle(), book.getAuthor().getId()
        );
    }
}
