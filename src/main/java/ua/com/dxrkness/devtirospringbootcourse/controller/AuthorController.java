package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.dxrkness.devtirospringbootcourse.domain.dto.AuthorDto;
import ua.com.dxrkness.devtirospringbootcourse.mappers.AuthorMapper;
import ua.com.dxrkness.devtirospringbootcourse.service.AuthorService;

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
        var savedEntity = authorService.create(toSave);

        return new ResponseEntity<>(authorMapper.entityToDto(savedEntity), HttpStatus.CREATED);
    }
}
