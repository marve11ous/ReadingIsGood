package com.readingisgood.service;

import com.readingisgood.models.request.BookOrderRequest;
import com.readingisgood.models.request.CreateBookRequest;
import com.readingisgood.models.request.UpdateBookRequest;
import com.readingisgood.models.response.BookResponse;

import java.util.List;
import java.util.Map;

public interface BookService {
    BookResponse saveBook(CreateBookRequest book);

    BookResponse updateBook(Long id, UpdateBookRequest updateBookRequest);

    BookResponse getBook(Long id);

    List<BookResponse> getBooks(int page, int size);

    Map<Long, Long> lockAvailableBooks(List<BookOrderRequest> request);

}
