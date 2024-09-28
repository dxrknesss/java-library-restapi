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
public class AuthorDaoJdbcTemplateTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AuthorDaoJdbcTemplate authorDao;

    @Test
    public void createAuthor_generatesValidSql() {
        var author = TestDataUtil.createTestAuthor();

        authorDao.create(author);

        Mockito.verify(jdbcTemplate).update(
                eq("INSERT INTO authors (id, name, age) VALUES (?, ?, ?)"),
                eq(author.getId()), eq(author.getName()), eq(author.getAge())
        );
    }

    @Test
    public void findOne_generatesValidSql() {
        var authorOptional = authorDao.findOne(1L);

        Mockito.verify(jdbcTemplate).query(
                eq("SELECT * FROM authors WHERE id = ? LIMIT 1"),
                ArgumentMatchers.<AuthorDaoJdbcTemplate.AuthorRowMapper>any(),
                eq(1L)
        );
    }

    @Test
    public void findAll_generatesValidSql() {
        var results = authorDao.findAll();

        Mockito.verify(jdbcTemplate).query(
                eq("SELECT * FROM authors"),
                ArgumentMatchers.<AuthorDaoJdbcTemplate.AuthorRowMapper>any()
        );
    }
}
