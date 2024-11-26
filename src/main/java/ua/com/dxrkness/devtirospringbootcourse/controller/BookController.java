package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        var toSave = bookMapper.dtoToEntity(bookDto);
        var savedEntity = bookService.save(isbn, toSave);

        return new ResponseEntity<>(bookMapper.entityToDto(savedEntity), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<BookDto>> getAllBooks(@RequestParam(name = "page") int page,
                                                     @RequestParam(name = "size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        var allBooksAsEntities = bookService.findAll(pageable);

        return new ResponseEntity<>(allBooksAsEntities.map(bookMapper::entityToDto), HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookDto> findBookById(@PathVariable("isbn") String bookIsbn) {
        var optionalEntity = bookService.findByIsbn(bookIsbn);

        return optionalEntity.map(bookEntity ->
                ResponseEntity.ok(bookMapper.entityToDto(bookEntity))
        ).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<BookDto> fullBookUpdate(@PathVariable("isbn") String isbn,
                                                  @RequestBody BookDto bookDto) {
        if (!bookService.doesExist(isbn)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        var updatedEntity = bookService.save(
                isbn, bookMapper.dtoToEntity(bookDto)
        );

        return ResponseEntity.ok(bookMapper.entityToDto(updatedEntity));
    }

    @PatchMapping("/{isbn}")
    public ResponseEntity<BookDto> partialBookUpdate(@PathVariable("isbn") String isbn,
                                                     @RequestBody BookDto bookDto) {
        if (!bookService.doesExist(isbn)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        var updatedEntity = bookService.partialUpdate(
                isbn, bookMapper.dtoToEntity(bookDto)
        );

        return ResponseEntity.ok(bookMapper.entityToDto(updatedEntity));
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<BookDto> deleteBook(@PathVariable("isbn") String isbn) {
        if (!bookService.doesExist(isbn)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        bookService.delete(isbn);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
