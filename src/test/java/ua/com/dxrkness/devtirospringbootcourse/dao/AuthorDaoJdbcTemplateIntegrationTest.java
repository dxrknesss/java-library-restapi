package ua.com.dxrkness.devtirospringbootcourse.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorDaoJdbcTemplateIntegrationTest {
    private final AuthorDaoJdbcTemplate authorDao;

    private Author singleAuthor;
    private List<Author> authorsList;

    @BeforeEach
    public void setup() {
        singleAuthor = TestDataUtil.createTestAuthorA();
        authorsList = TestDataUtil.createTestAuthorsList();
    }

    @Autowired
    public AuthorDaoJdbcTemplateIntegrationTest(AuthorDaoJdbcTemplate authorDao) {
        this.authorDao = authorDao;
    }

    @Test
    public void author_canBeCreated_andThen_retrieved() {
        authorDao.create(singleAuthor);

        var optionalAuthor = authorDao.findOne(singleAuthor.getId());

        Assertions.assertTrue(optionalAuthor.isPresent());
        Assertions.assertEquals(optionalAuthor.get(), singleAuthor);
    }

    @Test
    public void manyAuthors_canBeCreated_andThen_retrieved() {
        for (var author : authorsList) {
            authorDao.create(author);
        }

        var authorsFromDb = authorDao.findAll();

        Assertions.assertEquals(authorsList.size(), authorsFromDb.size());
        Assertions.assertIterableEquals(authorsList, authorsFromDb);
    }

    @Test
    public void author_thatExists_updatesCorrectly() {
        var authorId = singleAuthor.getId();
        authorDao.create(singleAuthor);

        singleAuthor.setId(222L);
        singleAuthor.setName("update");
        authorDao.update(authorId, singleAuthor);
        var updatedAuthor = authorDao.findOne(singleAuthor.getId());

        Assertions.assertTrue(updatedAuthor.isPresent());
        Assertions.assertEquals(updatedAuthor.get(), singleAuthor);
    }

    @Test
    public void author_thatExists_canBeDeleted() {
        authorDao.create(singleAuthor);

        authorDao.delete(singleAuthor.getId());
        var authorFromDb = authorDao.findOne(singleAuthor.getId());

        Assertions.assertTrue(authorFromDb.isEmpty());
    }
}
