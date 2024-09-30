package ua.com.dxrkness.devtirospringbootcourse.service;

import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import java.util.List;

public interface AuthorService {
    Author save(Author newAuthor);
    List<Author> findAll();
}
