package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ua.com.dxrkness.devtirospringbootcourse.TestDataUtil;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.service.AuthorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerIntegrationTest {
    private final WebTestClient testClient;
    private final AuthorService authorService;

    private static Author expectedAuthor;
    private static List<Author> allAuthors;
    private static final String updateName = "UPDATED!";
    private static final Author authorOnlyName = new Author(null, updateName, null);

    @Autowired
    public AuthorControllerIntegrationTest(AuthorService authorService, AuthorController authorController) {
        this.authorService = authorService;

        testClient = MockMvcWebTestClient.bindToController(authorController).build();
    }

    @BeforeEach
    public void setup() {
        expectedAuthor = TestDataUtil.createTestAuthorA();
        allAuthors = TestDataUtil.createTestAuthorsList();
    }

    @Test
    public void creatingAuthor_returnsCreatedAndEntity() {
        var result = testClient.post().uri("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(expectedAuthor)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Author.class)
                .returnResult();

        var actualAuthor = result.getResponseBody();
        assertNotNull(actualAuthor);

        // expected author will always have id 1, so for it to equal with actual, we need to update it
        expectedAuthor.setId(actualAuthor.getId());
        assertEquals(expectedAuthor, result.getResponseBody());
    }

    @Test
    public void gettingAllAuthors_returnsOkAndAllEntities() {
        allAuthors = authorService.saveAll(allAuthors);

        var result = testClient.get().uri("/authors")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<Author>>() {
                })
                .returnResult();
        var actualAuthors = result.getResponseBody();

        assertNotNull(actualAuthors);
        assertFalse(actualAuthors.isEmpty());

        actualAuthors.forEach(actual -> // way easier to do with map
                // update id for each expected author, otherwise they could differ with actual IDs
                allAuthors.stream()
                        .filter(expected -> expected.getName().equals(actual.getName()))
                        .findFirst().orElseThrow().setId(actual.getId())
        );
        assertTrue(actualAuthors.containsAll(allAuthors));
    }

    @Test
    public void gettingExistingAuthor_returnsOkAndEntity() {
        expectedAuthor = authorService.save(expectedAuthor);

        var result = testClient.get().uri("/authors/" + expectedAuthor.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Author.class)
                .returnResult();

        assertEquals(expectedAuthor, result.getResponseBody());
    }

    @Test
    public void gettingNonExistingAuthor_returnsNotFound() {
        testClient.get().uri("/authors/-1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void fullyUpdatingExistingAuthor_returnsOkAndEntity() {
        expectedAuthor = authorService.save(expectedAuthor);

        expectedAuthor.setName(updateName);
        expectedAuthor.setAge(222);

        var result = testClient.put().uri("/authors/" + expectedAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(expectedAuthor)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Author.class)
                .returnResult();

        assertEquals(expectedAuthor, result.getResponseBody());
    }

    @Test
    public void fullyUpdatingNonExistingAuthor_returnsNotFound() {
        expectedAuthor.setName(updateName);

        testClient.put().uri("/authors/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(expectedAuthor)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void partiallyUpdatingExistingAuthor_returnsOkAndEntity() {
        expectedAuthor = authorService.save(expectedAuthor);

        expectedAuthor.setName(updateName);

        var result = testClient.patch().uri("/authors/" + expectedAuthor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorOnlyName)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Author.class)
                .returnResult();

        assertEquals(expectedAuthor, result.getResponseBody());
    }

    @Test
    public void partiallyUpdatingNonExistingAuthor_returnsNotFound() {
        testClient.patch().uri("/authors/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorOnlyName)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void deletingExistingAuthor_returnsNoContent() {
        var savedId = authorService.save(expectedAuthor).getId().intValue();

        testClient.delete().uri("/authors/" + savedId)
                .exchange()
                .expectStatus().isNoContent();

        testClient.get().uri("/authors/" + savedId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void deletingNonExistingAuthor_returnsNotFound() {
        testClient.delete().uri("/authors/-1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
