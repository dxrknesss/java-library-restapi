package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

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
}
