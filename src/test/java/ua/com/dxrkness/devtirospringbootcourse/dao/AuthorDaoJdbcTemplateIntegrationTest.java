package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorDaoJdbcTemplateIntegrationTest {
    private AuthorDaoJdbcTemplate authorDao;

    @Autowired
    public AuthorDaoJdbcTemplateIntegrationTest(AuthorDaoJdbcTemplate authorDao) {
        this.authorDao = authorDao;
    }

    @Test
    public void author_canBeCreated_andThen_retrieved() {
        var author = TestDataUtil.createTestAuthorA();

        authorDao.create(author);
        var optionalAuthor = authorDao.findOne(author.getId());

        Assertions.assertTrue(optionalAuthor.isPresent());
        Assertions.assertEquals(optionalAuthor.get(), author);
    }

    @Test
    public void manyAuthors_canBeCreated_andThen_retrieved() {
        var dummyAuthors = TestDataUtil.createTestAuthorsList();
        for (var author : dummyAuthors) {
            authorDao.create(author);
        }

        var authorsFromDb = authorDao.findAll();

        Assertions.assertEquals(dummyAuthors.size(), authorsFromDb.size());
        Assertions.assertIterableEquals(dummyAuthors, authorsFromDb);
    }

    @Test
    public void author_thatIsCreated_updatesCorrectly() {
        var author = TestDataUtil.createTestAuthorA();
        var authorId = author.getId();
        authorDao.create(author);

        author.setId(222L);
        author.setName("update");
        authorDao.update(authorId, author);

        var updatedAuthor = authorDao.findOne(author.getId());

        Assertions.assertTrue(updatedAuthor.isPresent());
        Assertions.assertEquals(updatedAuthor.get(), author);
    }
}
