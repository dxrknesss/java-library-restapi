package ua.com.dxrkness.devtirospringbootcourse.dao;

import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

public final class TestDataUtil {
    private TestDataUtil() {
    }

    public static Author createTestAuthor() {
        return new Author(
                1L,
                "Omar Hayam",
                900
        );
    }

    public static Book createTestBook() {
        return new Book(
                "123-123",
                "Quatrians",
                new Author(1L, null, null)
        );
    }
}
