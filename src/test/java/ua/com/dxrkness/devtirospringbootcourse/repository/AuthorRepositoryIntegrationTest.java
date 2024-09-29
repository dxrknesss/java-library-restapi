package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;

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

    @Test
    public void manyAuthors_canBeCreated_andThen_retrieved() {
        authorRepository.saveAll(authorList);

        var authorsFromDb = (Collection<Author>) authorRepository.findAll();

        Assertions.assertEquals(authorList.size(), authorsFromDb.size());
        Assertions.assertIterableEquals(authorList, authorsFromDb);
    }

    @Test
    public void author_thatExists_updatesCorrectly() {
        authorRepository.save(singleAuthor);

        singleAuthor.setName("update");
        authorRepository.save(singleAuthor);
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

    @Test
    public void getAuthorsWithAgeLessThan_returnsValidAuthors() {
        authorRepository.saveAll(authorList);

        var authorsYoungerThan100 = authorRepository.findAllByAgeLessThan(100);

        Assertions.assertIterableEquals(
                Set.of(TestDataUtil.createTestAuthorB()),
                authorsYoungerThan100
        );
    }

    @Test
    public void getAuthorsWithAgeGreaterThan_returnsValidAuthors() {
        authorRepository.saveAll(authorList);

        var dbOldAuthors = authorRepository.findAllWithAgeGreaterThan(100);
        var dummyOldAuthors = Set.of(TestDataUtil.createTestAuthorA(), TestDataUtil.createTestAuthorC());

        MatcherAssert.assertThat(dbOldAuthors, containsInAnyOrder(dummyOldAuthors.toArray()));
    }
}
