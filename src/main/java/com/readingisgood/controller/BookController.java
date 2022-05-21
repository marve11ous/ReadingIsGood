package com.readingisgood.controller;

import com.readingisgood.models.request.CreateBookRequest;
import com.readingisgood.models.request.UpdateBookRequest;
import com.readingisgood.models.response.BookResponse;
import com.readingisgood.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.AbstractThrowableProblem;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Book management API")
@RequestMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController implements SecuredController {

    private final BookService booksService;

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new book")
    public BookResponse createBook(@Valid @RequestBody CreateBookRequest request) {
        return booksService.saveBook(request);
    }

    @PutMapping(value = "/{id}/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update book by identifier")
    @ApiResponse(responseCode = "404",
            content = @Content(schema = @Schema(implementation = AbstractThrowableProblem.class)))
    public BookResponse updateBook(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest request) {
        return booksService.updateBook(id, request);
    }

    @GetMapping(value = "/{id}/")
    @Operation(summary = "Get book by identifier")
    @ApiResponse(responseCode = "404",
            content = @Content(schema = @Schema(implementation = AbstractThrowableProblem.class)))
    public BookResponse getBook(@PathVariable Long id) {
        return booksService.getBook(id);
    }

    @GetMapping(value = "/")
    @Operation(summary = "Get all books (by pages)")
    public List<BookResponse> getBooks(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "100") int size) {
        return booksService.getBooks(page, size);
    }
}
