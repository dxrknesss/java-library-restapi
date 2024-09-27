package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.springframework.jdbc.core.JdbcTemplate;

public class AuthorDaoJdbcTemplate implements AuthorDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
