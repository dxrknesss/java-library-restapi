package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorRepositoryIntegrationTest {
    private final AuthorRepository authorRepository;

    private Author singleAuthor;
    private List<Author> authorList;

    @BeforeEach
    public void setup() {
        singleAuthor = TestDataUtil.createTestAuthorA();
        authorList = TestDataUtil.createTestAuthorsList();
    }

    @Autowired
    public AuthorRepositoryIntegrationTest(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Test
    public void author_canBeCreated_andThen_retrieved() {
        authorRepository.save(singleAuthor);

        var optionalAuthor = authorRepository.findById(singleAuthor.getId());

        Assertions.assertTrue(optionalAuthor.isPresent());
        Assertions.assertEquals(optionalAuthor.get(), singleAuthor);
    }

    /*
    @Test
    public void manyAuthors_canBeCreated_andThen_retrieved() {
        authorRepository.saveAll(authorList);

        var authorsFromDb = authorRepository.findAll();

        Assertions.assertEquals(authorList.size(), authorsFromDb.size());
        Assertions.assertIterableEquals(authorList, authorsFromDb); }

    @Test
    public void author_thatExists_updatesCorrectly() {
        var authorId = singleAuthor.getId();
        authorRepository.save(singleAuthor);

        singleAuthor.setId(222L);
        singleAuthor.setName("update");
        authorRepository.update(authorId, singleAuthor);
        var updatedAuthor = authorRepository.findById(singleAuthor.getId());

        Assertions.assertTrue(updatedAuthor.isPresent());
        Assertions.assertEquals(updatedAuthor.get(), singleAuthor);
    }

    @Test
    public void author_thatExists_canBeDeleted() {
        authorRepository.save(singleAuthor);

        authorRepository.delete(singleAuthor);
        var authorFromDb = authorRepository.findById(singleAuthor.getId());

        Assertions.assertTrue(authorFromDb.isEmpty());
    }
     */
}
