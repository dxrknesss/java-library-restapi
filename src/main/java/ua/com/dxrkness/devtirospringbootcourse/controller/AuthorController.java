package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.dxrkness.devtirospringbootcourse.domain.dto.AuthorDto;
import ua.com.dxrkness.devtirospringbootcourse.mappers.AuthorMapper;
import ua.com.dxrkness.devtirospringbootcourse.service.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorDto dto) {
        var toSave = authorMapper.dtoToEntity(dto);
        var savedEntity = authorService.save(toSave);

        return new ResponseEntity<>(authorMapper.entityToDto(savedEntity), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AuthorDto>> listAuthors() {
        var allAuthors = authorService.findAll();

        return ResponseEntity.ok(allAuthors.stream()
                .map(authorMapper::entityToDto)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> findAuthorById(@PathVariable("id") Long authorId) {
        var optionalEntity = authorService.findById(authorId);

        return optionalEntity.map(entity ->
                ResponseEntity.ok(authorMapper.entityToDto(entity))
        ).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> fullAuthorUpdate(@PathVariable("id") Long authorId,
                                                      @RequestBody AuthorDto updatedDto) {
        if (!authorService.doesExist(authorId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        updatedDto.setId(authorId);

        var updatedEntity = authorService.save(authorMapper.dtoToEntity(updatedDto));
        return ResponseEntity.ok(authorMapper.entityToDto(updatedEntity));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AuthorDto> partialAuthorUpdate(@PathVariable("id") Long authorId,
                                                         @RequestBody AuthorDto partiallyUpdatedDto) {
        if (!authorService.doesExist(authorId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        partiallyUpdatedDto.setId(authorId);

        var partiallyUpdatedEntity = authorService.partialUpdate(
                authorId, authorMapper.dtoToEntity(partiallyUpdatedDto)
        );
        return ResponseEntity.ok(authorMapper.entityToDto(partiallyUpdatedEntity));
    }
}
