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
import ua.com.dxrkness.devtirospringbootcourse.service.AuthorService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthorControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final AuthorService authorService;

    private Author author;

    @Autowired
    public AuthorControllerIntegrationTest(
            MockMvc mockMvc, ObjectMapper objectMapper,
            AuthorService authorService
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authorService = authorService;
    }

    @BeforeEach
    public void setup() {
        author = TestDataUtil.createTestAuthorA();
        author.setId(null);
    }

    @Test
    public void successfullyCreatingAuthor_returns201Code_andCreatedEntity() throws Exception {
        var authorAsJson = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorAsJson)
        ).andExpect(MockMvcResultMatchers.status().isCreated()).andExpectAll(
                MockMvcResultMatchers.jsonPath("$.id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.name").value(author.getName()),
                MockMvcResultMatchers.jsonPath("$.age").value(author.getAge())
        );
    }

    @Test
    public void listingAuthors_returns200Code_andAllAuthors() throws Exception {
        var allAuthors = TestDataUtil.createTestAuthorsList();
        allAuthors.stream().forEach(authorService::save);

        var allAuthorsAsJson = objectMapper.writeValueAsString(allAuthors);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(allAuthorsAsJson)
                );
    }

    @Test
    public void listingOneAuthorThatExists_returns200Code_andAuthor() throws Exception {
        authorService.save(author);

        author.setId(1L);
        var authorAsJson = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(authorAsJson)
                );
    }

    @Test
    public void listingOneAuthorThatDoesNotExist_returns404Code() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/authors/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void fullyUpdatingAuthorThatExists_returns200Code_andAuthor() throws Exception {
        authorService.save(author);

        author.setName("UPDATED");
        var updatedAuthorAsJson = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAuthorAsJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(updatedAuthorAsJson)
                );
    }

    @Test
    public void fullyUpdatingAuthorThatDoesNotExist_returns404Code() throws Exception {
        author.setName("UPDATED");
        var updatedAuthorAsJson = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/1023")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedAuthorAsJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void partiallyUpdatingAuthorThatExists_returns200Code_andAuthor() throws Exception {
        authorService.save(author);
        var updateName = "partial update";

        author.setId(null);
        author.setAge(null);
        author.setName(updateName);
        var partiallyUpdatedAuthorAsJson = objectMapper.writeValueAsString(author);

        author = authorService.findById(1L).get();
        author.setName(updateName);
        var expectedEntity = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partiallyUpdatedAuthorAsJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(expectedEntity)
                );
    }

    @Test
    public void partiallyUpdatingAuthorThatDoesNotExist_returns404Code() throws Exception {
        var updateName = "partial update";

        author.setId(null);
        author.setAge(null);
        author.setName(updateName);
        var partiallyUpdatedAuthorAsJson = objectMapper.writeValueAsString(author);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partiallyUpdatedAuthorAsJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deletingAuthorThatExists_returns204Code() throws Exception {
        authorService.save(author);

        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deletingAuthorThatDoesNotExist_returns404Code() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
