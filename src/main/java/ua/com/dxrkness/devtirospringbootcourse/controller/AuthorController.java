package ua.com.dxrkness.devtirospringbootcourse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.com.dxrkness.devtirospringbootcourse.domain.dto.AuthorDto;
import ua.com.dxrkness.devtirospringbootcourse.mappers.AuthorMapper;
import ua.com.dxrkness.devtirospringbootcourse.service.AuthorService;

@RestController
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @GetMapping("/authors")
    public AuthorDto createAuthor(@RequestBody AuthorDto dto) {
        var toSave = authorMapper.dtoToEntity(dto);
        var savedEntity = authorService.create(toSave);

        return authorMapper.entityToDto(savedEntity);
    }
}
