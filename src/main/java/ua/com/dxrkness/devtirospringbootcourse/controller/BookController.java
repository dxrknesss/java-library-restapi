package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.dxrkness.devtirospringbootcourse.domain.dto.BookDto;
import ua.com.dxrkness.devtirospringbootcourse.mappers.BookMapper;
import ua.com.dxrkness.devtirospringbootcourse.service.BookService;

import java.util.List;

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
        var toSave = bookMapper.dtoToEntity(bookDto);
        var savedEntity = bookService.save(isbn, toSave);

        return new ResponseEntity<>(bookMapper.entityToDto(savedEntity), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        var allBooksAsEntities = bookService.findAll();
        var allBooksAsDto = allBooksAsEntities.stream().map(bookMapper::entityToDto).toList();

        return new ResponseEntity<>(allBooksAsDto, HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookDto> findBookById(@PathVariable("isbn") String bookIsbn) {
        var optionalEntity = bookService.findByIsbn(bookIsbn);

        return optionalEntity.map(bookEntity ->
                ResponseEntity.ok(bookMapper.entityToDto(bookEntity))
        ).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
