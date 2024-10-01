package ua.com.dxrkness.devtirospringbootcourse.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, String>,
        PagingAndSortingRepository<Book, String> {
}
