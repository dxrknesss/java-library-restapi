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
        var author = TestDataUtil.createTestAuthorA();

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

    @Test
    public void update_generatesValidSql() {
        var author = TestDataUtil.createTestAuthorA();
        var authorId = author.getId();

        author.setName("UPDATED");
        author.setId(222L);
        authorDao.update(authorId, author);

        Mockito.verify(jdbcTemplate).update(
                eq("UPDATE authors SET id = ?, name = ?, age = ? WHERE id = ?"),
                eq(author.getId()), eq(author.getName()), eq(author.getAge()), eq(authorId)
        );
    }

    @Test
    public void delete_generatesValidSql() {
        var authorId = 2L;

        authorDao.delete(authorId);

        Mockito.verify(jdbcTemplate).update(
                eq("DELETE FROM authors WHERE id = ?"),
                eq(authorId)
        );
    }
}
