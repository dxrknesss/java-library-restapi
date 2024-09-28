package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
}
