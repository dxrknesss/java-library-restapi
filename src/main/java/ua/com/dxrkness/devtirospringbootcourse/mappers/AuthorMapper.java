package ua.com.dxrkness.devtirospringbootcourse.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.com.dxrkness.devtirospringbootcourse.domain.Author;
import ua.com.dxrkness.devtirospringbootcourse.domain.dto.AuthorDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {
    AuthorDto entityToDto(Author author);

    Author dtoToEntity(AuthorDto dto);
}
