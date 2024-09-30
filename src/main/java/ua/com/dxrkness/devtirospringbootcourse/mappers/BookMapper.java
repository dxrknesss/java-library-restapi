package ua.com.dxrkness.devtirospringbootcourse.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.com.dxrkness.devtirospringbootcourse.domain.Book;
import ua.com.dxrkness.devtirospringbootcourse.domain.dto.BookDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AuthorMapper.class)
public interface BookMapper {
    BookDto entityToDto(Book book);

    Book dtoToEntity(BookDto dto);
}
