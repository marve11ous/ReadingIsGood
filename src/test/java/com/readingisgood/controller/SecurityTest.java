package com.readingisgood.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityTest extends AbstractIT {

    private static Stream<Arguments> endpoints() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, "/statistics/orders/amount/"),
                Arguments.of(HttpMethod.GET, "/statistics/orders/total/"),
                Arguments.of(HttpMethod.GET, "/statistics/orders/books/"),
                Arguments.of(HttpMethod.GET, "/books/"),
                Arguments.of(HttpMethod.GET, "/books/1/"),
                Arguments.of(HttpMethod.GET, "/customers/"),
                Arguments.of(HttpMethod.GET, "/customers/1/orders/"),
                Arguments.of(HttpMethod.GET, "/orders/1"),
                Arguments.of(HttpMethod.GET, "/orders/")
        );
    }

    private static Stream<Arguments> modifyingEndpoints() {
        return Stream.of(
                Arguments.of(HttpMethod.POST, "/books/"),
                Arguments.of(HttpMethod.PUT, "/books/1/"),
                Arguments.of(HttpMethod.POST, "/customers/"),
                Arguments.of(HttpMethod.POST, "/orders/")
        );
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource({"endpoints", "modifyingEndpoints"})
    void assertUnauthorized(HttpMethod method, String path) {
        perform(request(method, path), null).andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource({"endpoints", "modifyingEndpoints"})
    void assertAccessDenied(HttpMethod method, String path) {
        perform(request(method, path).with(user("unknown").roles("NONE")), null)
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("modifyingEndpoints")
    void assertAccessDeniedToModify(HttpMethod method, String path) {
        perform(request(method, path).with(user("user").roles("USER")), null)
                .andExpect(status().isForbidden());
    }

}