package com.readingisgood.controller;

import com.readingisgood.models.request.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class CustomerControllerTest extends AbstractIT {

    private static Stream<Arguments> validationArgs() {
        return Stream.of(
                Arguments.of("name", " ", "must not be blank"),
                Arguments.of("name", null, "must not be blank"),
                Arguments.of("lastName", " ", "must not be blank"),
                Arguments.of("lastName", null, "must not be blank"),
                Arguments.of("birthDate", null, "must not be null"),
                Arguments.of("email", " ", "must not be blank"),
                Arguments.of("email", "snow", "must be a well-formed email address"),
                Arguments.of("email", null, "must not be blank"),
                Arguments.of("email", "who@test.com", "must be unique for each user")
        );
    }

    @Test
    void createCustomer() {
        assertResponse(post("/customers/"),
                new CreateUserRequest("Jon", "Snow", LocalDate.EPOCH, "snow@test.com"),
                "json/user-new.json"
        );
    }

    @ParameterizedTest
    @MethodSource("validationArgs")
    @Sql("/scripts/init-users.sql")
    void createCustomerValidation(String fieldName, Object fieldValue, String message) {
        var request = new CreateUserRequest("Jon", "Snow", LocalDate.EPOCH, "snow@test.com");
        assertMethodArgumentNotValid(post("/customers/"), request, fieldName, fieldValue, message);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    @Sql({"/scripts/init-users.sql", "/scripts/init-books.sql", "/scripts/init-orders.sql"})
    void getCustomerOrders(int id) {
        assertResponse(get("/customers/{id}/orders/", id), String.format("json/user-orders-%s.json", id));
    }

    @Test
    @Sql("/scripts/init-users.sql")
    void getCustomers() {
        assertResponse(get("/customers/"), "json/users.json");
    }
}