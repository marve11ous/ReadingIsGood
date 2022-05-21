package com.readingisgood.service;

import com.readingisgood.dao.BookRepository;
import com.readingisgood.dao.dto.Book;
import com.readingisgood.exceptions.BookNotFoundException;
import com.readingisgood.mapper.BookMapper;
import com.readingisgood.models.request.BookOrderRequest;
import com.readingisgood.models.request.CreateBookRequest;
import com.readingisgood.models.request.UpdateBookRequest;
import com.readingisgood.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Spy
    @SuppressWarnings("unused")
    private final BookMapper mapper = Mappers.getMapper(BookMapper.class);
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;

    @Test
    void saveBook() {
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(bookRepository).save(any(Book.class));

        var createBookRequest = new CreateBookRequest("Name", "Author", 1.2D, 3L);
        var bookResponse = bookService.saveBook(createBookRequest);
        assertEquals(createBookRequest.getName(), bookResponse.getName());
        assertEquals(createBookRequest.getAuthor(), bookResponse.getAuthor());
        assertEquals(createBookRequest.getPrice(), bookResponse.getPrice());
        assertEquals(createBookRequest.getStock(), bookResponse.getStock());
    }

    @Test
    void updateBook() {
        var book = new Book();
        book.setId(1L);
        book.setName("Name");
        book.setAuthor("Author");
        book.setPrice(1.3D);
        book.setStock(2L);
        doReturn(Optional.of(book)).when(bookRepository).getForUpdateById(eq(1L));

        var bookResponse = bookService.updateBook(1L, new UpdateBookRequest(6L));
        assertEquals(book.getId(), bookResponse.getId());
        assertEquals(book.getName(), bookResponse.getName());
        assertEquals(book.getAuthor(), bookResponse.getAuthor());
        assertEquals(book.getPrice(), bookResponse.getPrice());
        assertEquals(6L, bookResponse.getStock());
    }

    @Test
    void updateBookNotFound() {
        doReturn(Optional.empty()).when(bookRepository).getForUpdateById(eq(1L));
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, new UpdateBookRequest(6L)));
    }

    @Test
    void getBook() {
        var book = new Book();
        book.setId(1L);
        book.setName("Name");
        book.setAuthor("Author");
        book.setPrice(1.3D);
        book.setStock(2L);
        doReturn(Optional.of(book)).when(bookRepository).findById(eq(1L));

        var bookResponse = bookService.getBook(1L);
        assertEquals(book.getId(), bookResponse.getId());
        assertEquals(book.getName(), bookResponse.getName());
        assertEquals(book.getAuthor(), bookResponse.getAuthor());
        assertEquals(book.getPrice(), bookResponse.getPrice());
        assertEquals(2L, bookResponse.getStock());
    }

    @Test
    void getBooks() {
        var book = new Book();
        book.setId(1L);
        book.setName("Name");
        book.setAuthor("Author");
        book.setPrice(1.3D);
        book.setStock(2L);
        doReturn(new PageImpl<>(List.of(book))).when(bookRepository).findAll(eq(PageRequest.of(1, 3)));

        var bookResponses = bookService.getBooks(1, 3);
        assertEquals(1, bookResponses.size());
        var bookResponse = bookResponses.get(0);
        assertEquals(book.getId(), bookResponse.getId());
        assertEquals(book.getName(), bookResponse.getName());
        assertEquals(book.getAuthor(), bookResponse.getAuthor());
        assertEquals(book.getPrice(), bookResponse.getPrice());
        assertEquals(2L, bookResponse.getStock());
    }

    @Test
    void lockAvailableBooks() {
        var response = List.of(new Book(), new Book(), new Book());
        response.get(0).setId(1L);
        response.get(0).setStock(2L);
        response.get(1).setId(2L);
        response.get(1).setStock(5L);
        response.get(2).setId(3L);
        response.get(2).setStock(6L);
        doReturn(response).when(bookRepository).getAllForUpdateById(eq(Set.of(1L, 2L, 3L)));

        var request = List.of(
                new BookOrderRequest(1L, 3L),
                new BookOrderRequest(2L, 2L),
                new BookOrderRequest(3L, 6L)
        );
        assertEquals(Map.of(1L, -1L, 2L, 3L, 3L, 0L), bookService.lockAvailableBooks(request));
    }
}