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
    public List<Author> saveAll(List<Author> newAuthors) {
        return (List<Author>) authorRepository.saveAll(newAuthors);
    }

    @Override
    public List<Author> findAll() {
        return (List<Author>) authorRepository.findAll();
    }

    @Override
    public Optional<Author> findById(Long authorId) {
        return authorRepository.findById(authorId);
    }

    @Override
    public boolean doesExist(Long authorId) {
        return authorRepository.existsById(authorId);
    }

    @Override
    public Author partialUpdate(Long authorId, Author partiallyUpdatedAuthor) {
        return authorRepository.findById(authorId).map(authorFromDb -> {
            Optional.ofNullable(partiallyUpdatedAuthor.getAge()).ifPresent(authorFromDb::setAge);
            Optional.ofNullable(partiallyUpdatedAuthor.getName()).ifPresent(authorFromDb::setName);

            return authorRepository.save(authorFromDb);
        }).orElseThrow(() -> new RuntimeException("I'm too tired to write this. it's 3 in the morning and I need to get over with this project as fast as possible. "));
    }

    @Override
    public void delete(Long authorId) {
        authorRepository.deleteById(authorId);
    }
}
