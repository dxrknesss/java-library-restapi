package ua.com.dxrkness.devtirospringbootcourse.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
    public void successfullyCreatingBook_returns201CreatedCode_andCreatedEntity() throws Exception {
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
    public void listingBooks_returns200Code_andAllBooks() throws Exception {
        var allBooks = TestDataUtil.createTestBooksList();
        allBooks.stream()
                .forEach(book -> bookService.save(book.getIsbn(), book));
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
    public void listingOneBookThatExists_returns200Code_andBook() throws Exception {
        bookService.save(book.getIsbn(), book);

        book.setAuthor(bookService.findByIsbn(book.getIsbn()).get().getAuthor());
        var bookAsJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getIsbn()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(bookAsJson)
                );
    }

    @Test
    public void listingOneBookThatDoesNotExist_returns404Code() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books/1289043584593"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void fullyUpdatingBookThatExists_returns200Code_andBook() throws Exception {
        bookService.save(initialIsbn, book);

        book.setIsbn("3304040404404");
        book.setTitle(updatedTitle);
        book.setAuthor(bookService.findByIsbn(initialIsbn).get().getAuthor());
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
    public void fullyUpdatingBookThatDoesNotExist_returns404Code() throws Exception {
        book.setIsbn("3304040404404");
        book.setTitle(updatedTitle);
        book.setAuthor(new Author(2L, null, null));

        mockMvc.perform(MockMvcRequestBuilders.put("/books/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void partiallyUpdatingBookThatExists_returns200Code_andBook() throws Exception {
        bookService.save(initialIsbn, book);

        book.setIsbn(null);
        book.setAuthor(null);
        book.setTitle(updatedTitle);
        var partiallyUpdatedBookAsJson = objectMapper.writeValueAsString(book);

        book = bookService.findByIsbn(initialIsbn).get();
        book.setTitle(updatedTitle);
        var expectedEntity = objectMapper.writeValueAsString(book);

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
    public void partiallyUpdatingBookThatDoesNotExist_returns404Code() throws Exception {
        book.setIsbn(null);
        book.setAuthor(null);
        book.setTitle(updatedTitle);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + initialIsbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deletingBookThatExists_returns204Code() throws Exception {
        bookService.save(initialIsbn, book);

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + initialIsbn))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deletingBookThatDoesNotExist_returns404Code() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
