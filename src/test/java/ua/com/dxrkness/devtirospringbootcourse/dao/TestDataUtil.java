package ua.com.dxrkness.devtirospringbootcourse.dao;

import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

import java.util.List;

public final class TestDataUtil {
    private TestDataUtil() {
    }

    public static Author createTestAuthorA() {
        return new Author(1L, "Omar Hayam", 900);
    }

    public static Author createTestAuthorB() {
        return new Author(2L, "Stephen King", 70);
    }

    public static Author createTestAuthorC() {
        return new Author(3L, "William Shakespeare", 500);
    }

    public static List<Author> createTestAuthorsList() {
        return List.of(createTestAuthorA(), createTestAuthorB(), createTestAuthorC());
    }

    public static Book createTestBookA() {
        return new Book("123-123", "Quatrians", new Author(1L, null, null));
    }

    public static Book createTestBookB() {
        return new Book("456-456", "Mr. Mercedes", new Author(2L, null, null));
    }

    public static Book createTestBookC() {
        return new Book("789-789", "King Leare", new Author(3L, null, null));
    }

    public static List<Book> createTestBooksList() {
        return List.of(createTestBookA(), createTestBookB(), createTestBookC());
    }
}
