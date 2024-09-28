package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class AuthorDaoJdbcTemplate implements AuthorDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Author author) {
        jdbcTemplate.update(
                "INSERT INTO authors (id, name, age) VALUES (?, ?, ?)",
                author.getId(), author.getName(), author.getAge()
        );
    }

    @Override
    public Optional<Author> findOne(Long id) {
        var results = jdbcTemplate.query(
                "SELECT * FROM authors WHERE id = ? LIMIT 1",
                new AuthorRowMapper(),
                id
        );

        return results.stream().findFirst();
    }

    @Override
    public List<Author> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM authors",
                new AuthorRowMapper()
        );
    }

    @Override
    public void update(Long authorId, Author newAuthor) {
        jdbcTemplate.update(
                "UPDATE authors SET id = ?, name = ?, age = ? WHERE id = ?",
                newAuthor.getId(), newAuthor.getName(), newAuthor.getAge(),
                authorId
        );
    }

    public static class AuthorRowMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Author(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getInt("age")
            );
        }
    }
}
