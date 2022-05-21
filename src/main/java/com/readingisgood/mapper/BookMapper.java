package com.readingisgood.mapper;

import com.readingisgood.dao.dto.Book;
import com.readingisgood.models.request.CreateBookRequest;
import com.readingisgood.models.response.BookResponse;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {

    Book toBook(CreateBookRequest createBookRequest);

    BookResponse toBookResponse(Book book);
}
