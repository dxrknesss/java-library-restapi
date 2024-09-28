package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
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

    @Override
    public Optional<Book> findOne(String isbn) {
        var results = jdbcTemplate.query(
                "SELECT * FROM books WHERE isbn = ? LIMIT 1",
                new BookRowMapper(),
                isbn
        );

        return results.stream().findFirst();
    }

    public static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    new Author(rs.getLong("author_id"), null, null)
            );
        }
    }
}
