package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;

@DataJpaTest
@Transactional
public class AuthorRepositoryIntegrationTest {
    private final AuthorRepository authorRepository;

    private Author singleAuthor;
    private List<Author> authorList;

    @Autowired
    public AuthorRepositoryIntegrationTest(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @BeforeEach
    public void setup() {
        singleAuthor = TestDataUtil.createTestAuthorA();
        authorList = TestDataUtil.createTestAuthorsList();
    }

    @Test
    public void author_canBeCreated_andThen_retrieved() {
        var savedId = authorRepository.save(singleAuthor).getId();

        var optionalAuthor = authorRepository.findById(savedId);
        singleAuthor.setId(savedId);

        Assertions.assertTrue(optionalAuthor.isPresent());
        Assertions.assertEquals(optionalAuthor.get(), singleAuthor);
    }

    @Test
    public void manyAuthors_canBeCreated_andThen_retrieved() {
        authorRepository.saveAll(authorList);

        var authorsFromDb = (Collection<Author>) authorRepository.findAll();

        setIdsToNull(authorList);
        setIdsToNull(authorsFromDb);

        Assertions.assertEquals(authorList.size(), authorsFromDb.size());
        MatcherAssert.assertThat(authorsFromDb, containsInAnyOrder(authorList.toArray()));
    }

    @Test
    public void author_thatExists_updatesCorrectly() {
        authorRepository.save(singleAuthor);

        singleAuthor.setName("update");
        var savedId = authorRepository.save(singleAuthor).getId();
        var updatedAuthor = authorRepository.findById(savedId);
        singleAuthor.setId(savedId);

        Assertions.assertTrue(updatedAuthor.isPresent());
        Assertions.assertEquals(singleAuthor, updatedAuthor.get());
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
        var dummyAuthorsYoungerThan100 = Set.of(TestDataUtil.createTestAuthorB());

        setIdsToNull(authorsYoungerThan100);
        setIdsToNull(dummyAuthorsYoungerThan100);

        MatcherAssert.assertThat(
                authorsYoungerThan100,
                containsInAnyOrder(dummyAuthorsYoungerThan100.toArray())
        );
    }

    @Test
    public void getAuthorsWithAgeGreaterThan_returnsValidAuthors() {
        authorRepository.saveAll(authorList);

        var dbOldAuthors = authorRepository.findAllWithAgeGreaterThan(100);
        var dummyOldAuthors = Set.of(TestDataUtil.createTestAuthorA(), TestDataUtil.createTestAuthorC());

        setIdsToNull(dbOldAuthors);
        setIdsToNull(dummyOldAuthors);

        MatcherAssert.assertThat(dbOldAuthors, containsInAnyOrder(dummyOldAuthors.toArray()));
    }

    private void setIdsToNull(Collection<Author> collection) {
        collection.forEach(author -> author.setId(null));
    }
}
