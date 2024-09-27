package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

@ExtendWith(MockitoExtension.class)
public class AuthorDaoJdbcTemplateTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AuthorDaoJdbcTemplate authorDao;

    @Test
    public void createAuthor_generatesValidSql() {
        var author = new Author(1L, "Omar Hayam", 600);

        authorDao.create(author);

        Mockito.verify(jdbcTemplate).update(
                eq("INSERT INTO authors (id, name, age) VALUES (?, ?, ?)"),
                eq(1L), eq("Omar Hayam"), eq(600)
        );
    }
}
