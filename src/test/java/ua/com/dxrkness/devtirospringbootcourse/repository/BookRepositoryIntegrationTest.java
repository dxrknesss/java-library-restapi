package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;

@DataJpaTest
@Transactional
public class BookRepositoryIntegrationTest {
    private final BookRepository bookRepository;

    private Book singleBook;
    private List<Book> bookList;

    @Autowired
    public BookRepositoryIntegrationTest(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @BeforeEach
    public void setup() {
        singleBook = TestDataUtil.createTestBookA();
        bookList = TestDataUtil.createTestBooksList();
    }

    @Test
    public void book_canBeCreated_andThen_retrieved() {
        var saved = bookRepository.save(singleBook);

        var optionalBook = bookRepository.findById(singleBook.getIsbn());
        singleBook.setAuthor(saved.getAuthor());

        Assertions.assertTrue(optionalBook.isPresent());
        Assertions.assertEquals(singleBook, optionalBook.get());
    }

    @Test
    public void manyBooks_canBeCreated_andThen_retrieved() {
        bookRepository.saveAll(bookList);

        var booksFromDb = (Collection<Book>) bookRepository.findAll();

        booksFromDb.forEach(book -> book.getAuthor().setId(null));
        bookList.forEach(book -> book.getAuthor().setId(null));

        Assertions.assertEquals(bookList.size(), booksFromDb.size());
        MatcherAssert.assertThat(booksFromDb, containsInAnyOrder(bookList.toArray()));
    }

    @Test
    public void book_thatExists_updatesCorrectly() {
        bookRepository.save(singleBook);

        singleBook.setTitle("Updated");
        var saved = bookRepository.save(singleBook);
        var bookFromDb = bookRepository.findById(singleBook.getIsbn());
        singleBook.setAuthor(saved.getAuthor());

        Assertions.assertTrue(bookFromDb.isPresent());
        Assertions.assertEquals(singleBook, bookFromDb.get());
    }

    @Test
    public void book_thatExists_deletesCorrectly() {
        bookRepository.save(singleBook);

        bookRepository.delete(singleBook);
        var bookFromDb = bookRepository.findById(singleBook.getIsbn());

        Assertions.assertTrue(bookFromDb.isEmpty());
    }
}
