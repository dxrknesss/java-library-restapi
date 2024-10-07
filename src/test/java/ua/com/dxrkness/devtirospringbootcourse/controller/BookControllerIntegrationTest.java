package ua.com.dxrkness.devtirospringbootcourse.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import ua.com.dxrkness.devtirospringbootcourse.service.BookService;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final BookService bookService;

    private Book book;
    private final String initialIsbn = "123-123";
    private final String updatedTitle = "UPDATED!";


    @Autowired
    public BookControllerIntegrationTest(
            MockMvc mockMvc, ObjectMapper objectMapper,
            BookService bookService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.bookService = bookService;
    }

    @BeforeEach
    public void setup() {
        book = TestDataUtil.createTestBookB();
    }

    @Test
    public void creatingNew_shouldReturnCreatedAndEntity() throws Exception {
        var bookAsJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/books/" + initialIsbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookAsJson)
        ).andExpect(MockMvcResultMatchers.status().isCreated()).andExpectAll(
                MockMvcResultMatchers.jsonPath("$.isbn").value(initialIsbn),
                MockMvcResultMatchers.jsonPath("$.title").value(book.getTitle())
        );
    }

    @Test
    public void listingAll_shouldReturnOkAndAllEntities() throws Exception {
        var allBooks = TestDataUtil.createTestBooksList();
        allBooks.stream().forEach(book -> bookService.save(book.getIsbn(), book));
        allBooks = bookService.findAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.jsonPath("$.content", hasSize(3)),
                        MockMvcResultMatchers.jsonPath("$.content.[*].title",
                                containsInAnyOrder(allBooks.stream().map(Book::getTitle).toArray())),
                        MockMvcResultMatchers.jsonPath("$.content.[*].isbn",
                                containsInAnyOrder(allBooks.stream().map(Book::getIsbn).toArray())),
                        MockMvcResultMatchers.jsonPath("$.content.[*].author.id",
                                containsInAnyOrder(allBooks.stream().map(book -> book.getAuthor().getId().intValue()).toArray()))
                );
    }

    @Test
    public void listingExisting_returnsOkAndEntity() throws Exception {
        var savedToDb = bookService.save(book.getIsbn(), book);

        book.setAuthor(savedToDb.getAuthor());
        var bookAsJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getIsbn()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(bookAsJson)
                );
    }

    @Test
    public void listingNotExisting_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books/1289043584593"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void fullyUpdatingExisting_returnsOkAndEntity() throws Exception {
        var savedToDb = bookService.save(initialIsbn, book);

        book.setIsbn("3304040404404");
        book.setTitle(updatedTitle);
        book.setAuthor(savedToDb.getAuthor());
        var updatedBookAsJson = objectMapper.writeValueAsString(book);

        book.setIsbn(initialIsbn);
        var updatedBookWithInitIsbnAsJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.put("/books/" + initialIsbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookAsJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(updatedBookWithInitIsbnAsJson)
                );
    }

    @Test
    public void fullyUpdatingBookNotExisting_returnsNotFound() throws Exception {
        book.setIsbn("3304040404404");
        book.setTitle(updatedTitle);
        book.setAuthor(new Author(2L, null, null));

        mockMvc.perform(MockMvcRequestBuilders.put("/books/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void partiallyUpdatingExisting_returnsOkAndEntity() throws Exception {
        var savedToDb = bookService.save(initialIsbn, book);

        book.setIsbn(null);
        book.setAuthor(null);
        book.setTitle(updatedTitle);
        var partiallyUpdatedBookAsJson = objectMapper.writeValueAsString(book);

        savedToDb.setTitle(updatedTitle);
        var expectedEntity = objectMapper.writeValueAsString(savedToDb);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + initialIsbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partiallyUpdatedBookAsJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(expectedEntity)
                );
    }

    @Test
    public void partiallyUpdatingNonExisting_returnsNotFound() throws Exception {
        book.setIsbn(null);
        book.setAuthor(null);
        book.setTitle(updatedTitle);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + initialIsbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deletingExisting_returnsNoContent() throws Exception {
        bookService.save(initialIsbn, book);

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + initialIsbn))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deletingNonExisting_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
