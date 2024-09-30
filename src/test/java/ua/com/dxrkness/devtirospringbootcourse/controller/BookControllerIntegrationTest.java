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
}
