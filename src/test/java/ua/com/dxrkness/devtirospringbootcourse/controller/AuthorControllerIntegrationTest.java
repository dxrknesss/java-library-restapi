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
import ua.com.dxrkness.devtirospringbootcourse.service.AuthorService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthorControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final AuthorService authorService;

    private Author author;
    private List<Author> allAuthors;
    private final String updateName = "UPDATED!";

    @Autowired
    public AuthorControllerIntegrationTest(
            MockMvc mockMvc, ObjectMapper objectMapper,
            AuthorService authorService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authorService = authorService;
    }

    @BeforeEach
    public void setup() {
        author = TestDataUtil.createTestAuthorA();
        allAuthors = TestDataUtil.createTestAuthorsList();
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
        var savedIds = new ArrayList<Integer>(allAuthors.size());
        allAuthors.forEach(author -> {
            author.setId(null);
            savedIds.add(authorService.save(author).getId().intValue());
        });

        mockMvc.perform(MockMvcRequestBuilders.get("/authors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.[*]", hasSize(allAuthors.size())),
                        MockMvcResultMatchers.jsonPath("$.[*].id",
                                containsInAnyOrder(savedIds.toArray())),
                        MockMvcResultMatchers.jsonPath("$.[*].name",
                                containsInAnyOrder(allAuthors.stream().map(Author::getName).toArray())),
                        MockMvcResultMatchers.jsonPath("$.[*].age",
                                containsInAnyOrder(allAuthors.stream().map(Author::getAge).toArray()))
                );
    }

    @Test
    public void listingOneAuthorThatExists_returns200Code_andAuthor() throws Exception {
        var savedId = authorService.save(author).getId().intValue();

        mockMvc.perform(MockMvcRequestBuilders.get("/authors/" + savedId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.id", equalTo(savedId)),
                        MockMvcResultMatchers.jsonPath("$.name", equalTo(author.getName())),
                        MockMvcResultMatchers.jsonPath("$.age", equalTo(author.getAge()))
                );
    }

    @Test
    public void listingOneAuthorThatDoesNotExist_returns404Code() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/authors/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void fullyUpdatingAuthorThatExists_returns200Code_andAuthor() throws Exception {
        var savedId = authorService.save(author).getId().intValue();

        author.setName(updateName);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.id", equalTo(savedId)),
                        MockMvcResultMatchers.jsonPath("$.name", equalTo(author.getName())),
                        MockMvcResultMatchers.jsonPath("$.age", equalTo(author.getAge()))
                );
    }

    @Test
    public void fullyUpdatingAuthorThatDoesNotExist_returns404Code() throws Exception {
        author.setName(updateName);

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(author))
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void partiallyUpdatingAuthorThatExists_returns200Code_andAuthor() throws Exception {
        var savedId = authorService.save(author).getId().intValue();
        var initAge = author.getAge();

        author.setId(null);
        author.setAge(null);
        author.setName(updateName);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/" + savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.id", equalTo(savedId)),
                        MockMvcResultMatchers.jsonPath("$.name", equalTo(updateName)),
                        MockMvcResultMatchers.jsonPath("$.age", equalTo(initAge))
                );
    }

    @Test
    public void partiallyUpdatingAuthorThatDoesNotExist_returns404Code() throws Exception {
        author.setId(null);
        author.setAge(null);
        author.setName(updateName);

        mockMvc.perform(MockMvcRequestBuilders.patch("/authors/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(author)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deletingAuthorThatExists_returns204Code() throws Exception {
        var savedId = authorService.save(author).getId().intValue();

        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/" + savedId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deletingAuthorThatDoesNotExist_returns404Code() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/authors/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
