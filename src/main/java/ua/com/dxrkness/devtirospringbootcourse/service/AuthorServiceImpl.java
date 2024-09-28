package ua.com.dxrkness.devtirospringbootcourse.service;


import org.springframework.stereotype.Service;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.repository.AuthorRepository;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author create(Author newAuthor) {
        return authorRepository.save(newAuthor);
    }
}
