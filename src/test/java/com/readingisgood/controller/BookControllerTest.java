package com.readingisgood.controller;

import com.readingisgood.exceptions.BookNotFoundException;
import com.readingisgood.models.request.CreateBookRequest;
import com.readingisgood.models.request.UpdateBookRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

class BookControllerTest extends AbstractIT {

    private static Stream<Arguments> createValidationArgs() {
        return Stream.of(
                Arguments.of("price", 0D, "must be greater than 0"),
                Arguments.of("price", null, "must not be null"),
                Arguments.of("stock", -3L, "must be greater than or equal to 0"),
                Arguments.of("stock", null, "must not be null"),
                Arguments.of("name", " ", "must not be blank"),
                Arguments.of("name", null, "must not be blank"),
                Arguments.of("author", " ", "must not be blank"),
                Arguments.of("author", null, "must not be blank")
        );
    }

    private static Stream<Arguments> updateValidationArgs() {
        return Stream.of(
                Arguments.of("stock", -3L, "must be greater than or equal to 0"),
                Arguments.of("stock", null, "must not be null")
        );
    }

    @Test
    @SneakyThrows
    void createBook() {
        assertResponse(
                post("/books/"),
                new CreateBookRequest("Song of Ice & Fire", "George Martin", 9.99D, 13L),
                "json/book-new-1.json"
        );
        assertResponse(
                post("/books/"),
                new CreateBookRequest("Lord of the Rings", "Tolkien", 10D, 1L),
                "json/book-new-2.json"
        );
    }

    @ParameterizedTest
    @MethodSource("createValidationArgs")
    void createBookValidation(String fieldName, Object fieldValue, String message) {
        var request = new CreateBookRequest("Song of Ice & Fire", "George Martin", 10.2D, 3L);
        assertMethodArgumentNotValid(post("/books/"), request, fieldName, fieldValue, message);
    }

    @Test
    @Sql("/scripts/init-books.sql")
    void updateBook() {
        assertResponse(
                put("/books/2/"),
                new UpdateBookRequest(15L),
                "json/book-2-update.json"
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @Sql("/scripts/init-books.sql")
    void getBook(int id) {
        assertResponse(
                get("/books/{id}/", id),
                String.format("json/book-%s.json", id)
        );
    }

    @Test
    void getBookNotFound() {
        assertErrorResponse(
                get("/books/1/"),
                null,
                HttpStatus.NOT_FOUND,
                BookNotFoundException.class,
                "Book with id 1 not found"
        );
    }

    @Test
    @Sql("/scripts/init-books.sql")
    void getBooks() {
        assertResponse(get("/books/"), "json/books.json");
    }

    @Test
    void updateBookNotFound() {
        assertErrorResponse(
                put("/books/3/"),
                new UpdateBookRequest(15L),
                HttpStatus.NOT_FOUND,
                BookNotFoundException.class,
                "Book with id 3 not found"
        );
    }

    @ParameterizedTest
    @MethodSource("updateValidationArgs")
    void updateBookValidation(String fieldName, Object fieldValue, String message) {
        var request = new UpdateBookRequest(3L);
        assertMethodArgumentNotValid(put("/books/123/"), request, fieldName, fieldValue, message);
    }
}