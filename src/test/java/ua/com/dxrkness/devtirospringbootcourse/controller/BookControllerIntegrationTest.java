package ua.com.dxrkness.devtirospringbootcourse.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import ua.com.dxrkness.devtirospringbootcourse.service.BookService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final BookService bookService;

    private Book book;

    @Autowired
    public BookControllerIntegrationTest(
            MockMvc mockMvc, ObjectMapper objectMapper,
            BookService bookService
    ) {
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
        var bookIsbn = "123-123";

        mockMvc.perform(
                MockMvcRequestBuilders.post("/books/" + bookIsbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookAsJson)
        ).andExpect(MockMvcResultMatchers.status().isCreated()).andExpectAll(
                MockMvcResultMatchers.jsonPath("$.isbn").value(bookIsbn),
                MockMvcResultMatchers.jsonPath("$.title").value(book.getTitle())
        );
    }

    @Test
    public void listingBooks_returns200Code_andAllBooks() throws Exception {
        var allBooks = TestDataUtil.createTestBooksList();
        allBooks.stream()
                .forEach(book -> bookService.save(book.getIsbn(), book));
        var allBooksAsJson = objectMapper.writeValueAsString(allBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(allBooksAsJson));
    }

    @Test
    public void listingOneBookThatExists_returns200Code_andBook() throws Exception {
        bookService.save(book.getIsbn(), book);

        book.getAuthor().setId(1L);
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
        var initialIsbn = book.getIsbn();
        bookService.save(book.getIsbn(), book);

        book.setIsbn("3304040404404");
        book.setTitle("UPDATED");
        book.setAuthor(new Author(2L, null, null));
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
        book.setTitle("UPDATED");
        book.setAuthor(new Author(2L, null, null));
        var updatedBookAsJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.put("/books/44444")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookAsJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void partiallyUpdatingBookThatExists_returns200Code_andBook() throws Exception {
        var initialIsbn = book.getIsbn();
        var updateName = "partially updated!";

        bookService.save(initialIsbn, book);

        book.setIsbn(null);
        book.setAuthor(null);
        book.setTitle(updateName);
        var partiallyUpdatedBookAsJson = objectMapper.writeValueAsString(book);

        book = bookService.findByIsbn(initialIsbn).get();
        book.setTitle(updateName);
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
        var initialIsbn = book.getIsbn();
        var updateName = "partial update";

        book.setIsbn(null);
        book.setAuthor(null);
        book.setTitle(updateName);
        var partiallyUpdatedBookAsJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(MockMvcRequestBuilders.patch("/books/" + initialIsbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partiallyUpdatedBookAsJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deletingBookThatExists_returns204Code() throws Exception {
        bookService.save(book.getIsbn(), book);

        mockMvc.perform(MockMvcRequestBuilders.delete("/books/" + book.getIsbn()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deletingBookThatDoesNotExist_returns404Code() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/0"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
