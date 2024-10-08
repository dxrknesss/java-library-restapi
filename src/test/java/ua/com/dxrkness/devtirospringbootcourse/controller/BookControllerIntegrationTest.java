package ua.com.dxrkness.devtirospringbootcourse.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import ua.com.dxrkness.devtirospringbootcourse.repository.BookRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final BookRepository bookRepository;

    private final Book book = TestDataUtil.createTestBookB();
    private final String updatedTitle = "UPDATED!";

    @Autowired
    public BookControllerIntegrationTest(
            MockMvc mockMvc, ObjectMapper objectMapper,
            BookRepository bookRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.bookRepository = bookRepository;
    }

    @AfterEach
    public void teardown() {
        bookRepository.deleteAll();
    }

    @Test
    public void creatingNew_shouldReturnCreatedAndEntity() throws Exception {
        var initialIsbn = "123-123";

        var responseBody = mockMvc.perform(post("/books/" + initialIsbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var actualBook = objectMapper.readValue(responseBody, Book.class);

        Assertions.assertEquals(book.getTitle(), actualBook.getTitle());
        Assertions.assertEquals(initialIsbn, actualBook.getIsbn());
        Assertions.assertEquals(book.getAuthor(), actualBook.getAuthor());
    }

    @Test
    public void listingAll_shouldReturnOkAndAllEntities() throws Exception {
        var expectedBooks = bookRepository.saveAll(TestDataUtil.createTestBooksList());

        var responseBody = mockMvc.perform(get("/books"))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        List<Book> actualBooks = objectMapper
                .readerForListOf(Book.class)
                .readValue(objectMapper.readTree(responseBody).findValue("content"));

        Assertions.assertIterableEquals(expectedBooks, actualBooks);
    }

    @Test
    public void listingExisting_returnsOkAndEntity() throws Exception {
        var savedToDb = bookRepository.save(book);

        var responseBody = mockMvc.perform(get("/books/" + book.getIsbn()))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(savedToDb, objectMapper.readValue(responseBody, Book.class));
    }

    @Test
    public void listingNotExisting_returnsNotFound() throws Exception {
        mockMvc.perform(get("/books/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void fullyUpdatingExisting_returnsOkAndEntity() throws Exception {
        var savedToDb = bookRepository.save(book);
        var customBook = new Book("3304040404404", updatedTitle, new Author(222L, updatedTitle, 2));

        var responseBody = mockMvc.perform(put("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customBook)))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        var actualBook = objectMapper.readValue(responseBody, Book.class);

        Assertions.assertEquals(savedToDb.getIsbn(), actualBook.getIsbn());
        Assertions.assertEquals(customBook.getAuthor().getName(), actualBook.getAuthor().getName());
        Assertions.assertEquals(customBook.getAuthor().getAge(), actualBook.getAuthor().getAge());
        Assertions.assertEquals(customBook.getTitle(), actualBook.getTitle());
    }

    @Test
    public void fullyUpdatingNotExisting_returnsNotFound() throws Exception {
        var customBook = new Book("3304040404404", updatedTitle, new Author(2L, null, null));

        mockMvc.perform(put("/books/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void partiallyUpdatingExisting_returnsOkAndEntity() throws Exception {
        var savedToDb = bookRepository.save(book);
        var customBook = new Book(null, updatedTitle, null);

        var responseBody = mockMvc.perform(patch("/books/" + book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customBook)))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        var actualBook = objectMapper.readValue(responseBody, Book.class);

        Assertions.assertEquals(customBook.getTitle(), actualBook.getTitle());
        Assertions.assertEquals(savedToDb.getIsbn(), actualBook.getIsbn());
        Assertions.assertEquals(savedToDb.getAuthor(), actualBook.getAuthor());
    }

    @Test
    public void partiallyUpdatingNonExisting_returnsNotFound() throws Exception {
        var customBook = new Book(null, updatedTitle, null);

        mockMvc.perform(patch("/books/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletingExisting_returnsNoContent() throws Exception {
        bookRepository.save(book);

        mockMvc.perform(delete("/books/" + book.getIsbn()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deletingNonExisting_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/books/-1"))
                .andExpect(status().isNotFound());
    }
}
