package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;

import java.util.Set;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
    Set<Author> findAllByAgeLessThan(int age);
    @Query("SELECT aut FROM Author aut WHERE aut.age > ?1")
    Set<Author> findAllWithAgeGreaterThan(int age);
}
