package com.readingisgood.controller;

import com.readingisgood.models.request.BookOrderRequest;
import com.readingisgood.models.request.CreateOrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class OrderControllerTest extends AbstractIT {

    private static Stream<Arguments> validationArgs() {
        return Stream.of(
                Arguments.of("userId", 0L, "must be greater than 0"),
                Arguments.of("userId", null, "must not be null"),
                Arguments.of("books", null, "must not be empty"),
                Arguments.of("books", List.of(), "must not be empty"),
                Arguments.of("books[0].id", 0L, "must be greater than 0"),
                Arguments.of("books[0].id", null, "must not be null"),
                Arguments.of("books[0].amount", 0L, "must be greater than 0"),
                Arguments.of("books[0].amount", null, "must not be null")
        );
    }

    @Test
    @Sql({"/scripts/init-users.sql", "/scripts/init-books.sql"})
    void createOrderFailed() {
        assertResponse(
                post("/orders/"),
                new CreateOrderRequest(1L, List.of(new BookOrderRequest(1L, 15L))),
                "json/order-new-failed.json"
        );
    }

    @Test
    @Sql({"/scripts/init-users.sql", "/scripts/init-books.sql"})
    void createOrderOk() {
        assertResponse(
                post("/orders/"),
                new CreateOrderRequest(1L, List.of(new BookOrderRequest(1L, 5L))),
                "json/order-new-ok.json"
        );
    }

    @ParameterizedTest
    @MethodSource("validationArgs")
    void createOrderValidation(String fieldName, Object fieldValue, String message) {
        var request = new CreateOrderRequest(1L, List.of(new BookOrderRequest(1L, 1L)));
        assertMethodArgumentNotValid(post("/orders/"), request, fieldName, fieldValue, message);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    @Sql({"/scripts/init-users.sql", "/scripts/init-books.sql", "/scripts/init-orders.sql"})
    void getOrder(int id) {
        assertResponse(get("/orders/{id}/", id), String.format("json/order-%s.json", id));
    }

    @Test
    @Sql({"/scripts/init-users.sql", "/scripts/init-books.sql", "/scripts/init-orders.sql"})
    void getOrdersInRange() {
        assertResponse(get("/orders/?startDate={start}&endDate={end}",
                        LocalDateTime.parse("2022-04-18T00:00:00"), LocalDateTime.parse("2022-04-20T09:00:00")),
                "json/orders-1.json");
    }

    @Test
    @Sql({"/scripts/init-users.sql", "/scripts/init-books.sql", "/scripts/init-orders.sql"})
    void getOrdersFrom() {
        assertResponse(get("/orders/?startDate={start}", LocalDateTime.parse("2022-04-20T11:00:00")),
                "json/orders-2.json");
    }

    @Test
    void getOrdersValidation() {
        assertErrorResponse(
                get("/orders/?endDate={value}", LocalDateTime.now()), null,
                HttpStatus.BAD_REQUEST,
                MissingServletRequestParameterException.class,
                "Required request parameter 'startDate' for method parameter type LocalDateTime is not present"
        );
    }

}