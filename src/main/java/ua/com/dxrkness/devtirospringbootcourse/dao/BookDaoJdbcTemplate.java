package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.springframework.jdbc.core.JdbcTemplate;

public class BookDaoJdbcTemplate implements BookDao {
    private final JdbcTemplate jdbcTemplate;

    public BookDaoJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
