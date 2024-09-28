package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthorDaoJdbcTemplateIntegrationTest {
    private AuthorDaoJdbcTemplate authorDao;

    @Autowired
    public AuthorDaoJdbcTemplateIntegrationTest(AuthorDaoJdbcTemplate authorDao) {
        this.authorDao = authorDao;
    }

    @Test
    public void author_canBeCreated_andThen_retrieved() {
        var author = TestDataUtil.createTestAuthor();

        authorDao.create(author);
        var optionalAuthor = authorDao.findOne(author.getId());

        Assertions.assertTrue(optionalAuthor.isPresent());
        Assertions.assertEquals(optionalAuthor.get(), author);
    }
}
