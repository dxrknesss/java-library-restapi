package ua.com.dxrkness.devtirospringbootcourse.service;


import org.springframework.stereotype.Service;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author save(Author newAuthor) {
        return authorRepository.save(newAuthor);
    }

    @Override
    public List<Author> findAll() {
        return StreamSupport.stream(
                authorRepository.findAll().spliterator(),
                false
        ).toList();
    }

    @Override
    public Optional<Author> findById(Long authorId) {
        return authorRepository.findById(authorId);
    }

    @Override
    public boolean doesExist(Long authorId) {
        return authorRepository.existsById(authorId);
    }
}
