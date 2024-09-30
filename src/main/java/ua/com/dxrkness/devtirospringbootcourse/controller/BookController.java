package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.dxrkness.devtirospringbootcourse.domain.dto.BookDto;
import ua.com.dxrkness.devtirospringbootcourse.mappers.BookMapper;
import ua.com.dxrkness.devtirospringbootcourse.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookMapper bookMapper;
    private final BookService bookService;

    public BookController(BookMapper bookMapper, BookService bookService) {
        this.bookMapper = bookMapper;
        this.bookService = bookService;
    }

    @PostMapping("/{isbn}")
    public ResponseEntity<BookDto> createBook(@PathVariable("isbn") String isbn,
                                              @RequestBody BookDto bookDto) {
        System.out.println(bookDto);
        var toSave = bookMapper.dtoToEntity(bookDto);
        System.out.println(toSave);
        var savedEntity = bookService.save(isbn, toSave);

        System.out.println(bookMapper.entityToDto(savedEntity));
        return new ResponseEntity<>(bookMapper.entityToDto(savedEntity), HttpStatus.CREATED);
    }
}
