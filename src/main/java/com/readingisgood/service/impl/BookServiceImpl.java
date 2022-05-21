package com.readingisgood.service.impl;

import com.readingisgood.dao.BookRepository;
import com.readingisgood.dao.dto.Book;
import com.readingisgood.exceptions.BookNotFoundException;
import com.readingisgood.mapper.BookMapper;
import com.readingisgood.models.request.BookOrderRequest;
import com.readingisgood.models.request.CreateBookRequest;
import com.readingisgood.models.request.UpdateBookRequest;
import com.readingisgood.models.response.BookResponse;
import com.readingisgood.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository repository;
    private final BookMapper mapper;

    @Override
    public BookResponse saveBook(CreateBookRequest createBookRequest) {
        var book = mapper.toBookResponse(repository.save(mapper.toBook(createBookRequest)));
        log.info("Created {}", book);
        return book;
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest updateBookRequest) {
        var book = repository.getForUpdateById(id).orElseThrow(() -> new BookNotFoundException(id));
        book.setStock(updateBookRequest.getStock());
        var response = mapper.toBookResponse(book);
        log.info("Updated {}", book);
        return response;
    }

    @Override
    public BookResponse getBook(Long id) {
        return repository.findById(id).map(mapper::toBookResponse)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    public List<BookResponse> getBooks(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).stream()
                .map(mapper::toBookResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> lockAvailableBooks(List<BookOrderRequest> request) {
        var books = request.stream()
                .collect(Collectors.toMap(BookOrderRequest::getId, BookOrderRequest::getAmount));
        return StreamSupport.stream(repository.getAllForUpdateById(books.keySet()).spliterator(), false)
                .collect(Collectors.toMap(
                        Book::getId,
                        book -> book.getStock() - books.get(book.getId())
                ));
    }
}
