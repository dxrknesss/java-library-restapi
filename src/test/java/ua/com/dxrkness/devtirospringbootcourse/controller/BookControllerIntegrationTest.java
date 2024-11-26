package ua.com.dxrkness.devtirospringbootcourse.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import ua.com.dxrkness.devtirospringbootcourse.repository.BookRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIntegrationTest {
    private final WebTestClient testClient;
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    private static Book expectedBook;
    private static List<Book> expectedBookList;
    private static final String updatedTitle = "UPDATED!";

    @Autowired
    public BookControllerIntegrationTest(BookRepository bookRepository, BookController bookController) {
        this.bookRepository = bookRepository;
        this.objectMapper = new ObjectMapper();

        testClient = MockMvcWebTestClient
                .bindToController(bookController)
                .build();
    }

    @BeforeEach
    public void setup() {
        expectedBook = TestDataUtil.createTestBookA();
        expectedBookList = TestDataUtil.createTestBooksList();
    }

    @AfterEach
    public void teardown() {
        bookRepository.deleteAll();
    }

    @Test
    public void creatingNew_shouldReturnCreatedAndEntity() {
        var initialIsbn = "123-123";

        var response = testClient.post().uri("/books/" + initialIsbn)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class)
                .returnResult();
        var actualBook = response.getResponseBody();

        assertNotNull(actualBook);
        expectedBook.getAuthor().setId(actualBook.getAuthor().getId()); // sync with author in database
        assertEquals(expectedBook, actualBook);
    }

    @Test
    public void listingAll_shouldReturnOkAndAllEntities() {
        List<Book> expectedBooks = (List<Book>) bookRepository.saveAll(expectedBookList);

        var response = testClient.get().uri("/books?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .returnResult();
        var responseBody = response.getResponseBody();
        assertNotNull(responseBody);

        List<Book> actualBooks;
        try {
            actualBooks = objectMapper
                    .readerForListOf(Book.class)
                    .readValue(objectMapper.readTree(responseBody).findValue("content"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(expectedBooks.containsAll(actualBooks));
    }

    @Test
    public void listingExisting_returnsOkAndEntity() {
        expectedBook = bookRepository.save(expectedBook);

        var response = testClient.get().uri("/books/" + expectedBook.getIsbn())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Book.class)
                .returnResult();
        var actualBook = response.getResponseBody();

        assertNotNull(actualBook);
        assertEquals(expectedBook, actualBook);
    }

    @Test
    public void listingNotExisting_returnsNotFound() {
        testClient.get().uri("/books/-1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void fullyUpdatingExisting_returnsOkAndEntity() {
        expectedBook = bookRepository.save(expectedBook);
        var customBook = TestDataUtil.createTestBookC();

        var response = testClient.put().uri("/books/" + expectedBook.getIsbn())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customBook)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Book.class)
                .returnResult();
        var actualBook = response.getResponseBody();

        assertNotNull(actualBook);
        customBook.setIsbn(expectedBook.getIsbn());
        assertEquals(customBook, actualBook);
    }

    @Test
    public void fullyUpdatingNotExisting_returnsNotFound() {
        testClient.put().uri("/books/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void partiallyUpdatingExisting_returnsOkAndEntity() {
        expectedBook = bookRepository.save(expectedBook);
        var customBook = new Book(null, updatedTitle, null);

        var response = testClient.patch().uri("/books/" + expectedBook.getIsbn())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customBook)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Book.class)
                .returnResult();
        var actualBook = response.getResponseBody();

        assertNotNull(actualBook);
        expectedBook.setTitle(updatedTitle);
        assertEquals(expectedBook, actualBook);
    }

    @Test
    public void partiallyUpdatingNonExisting_returnsNotFound() {
        var customBook = new Book(null, updatedTitle, null);

        testClient.patch().uri("/books/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customBook)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void deletingExisting_returnsNoContent() {
        bookRepository.save(expectedBook);

        testClient.delete().uri("/books/" + expectedBook.getIsbn())
                .exchange()
                .expectStatus().isNoContent();

        testClient.get().uri("/books/" + expectedBook.getIsbn())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void deletingNonExisting_returnsNotFound() {
        testClient.delete().uri("/books/-1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
