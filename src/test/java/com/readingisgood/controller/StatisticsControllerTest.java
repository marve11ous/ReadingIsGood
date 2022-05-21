package com.readingisgood.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Sql({"/scripts/init-users.sql", "/scripts/init-books.sql", "/scripts/init-orders.sql"})
class StatisticsControllerTest extends AbstractIT {

    private static Stream<String> statisticsParams() {
        return Stream.of(
                "startMonth=2022-04&endMonth=2022-04",
                "startMonth=2021-01&endMonth=2022-01",
                "startMonth=2021-01"
        );
    }

    private static Stream<String> validationParams() {
        return Stream.of(
                "/statistics/orders/amount/",
                "/statistics/orders/total/",
                "/statistics/orders/books/"
        ).flatMap(path -> Stream.of(
                path + "?startMonth=2022",
                path + "?startMonth=2021-01&endMonth=2022-01-01"
        ));
    }

    @ParameterizedTest
    @MethodSource("statisticsParams")
    void getOrdersTotal(String params) {
        assertResponse(
                get("/statistics/orders/total/?" + params),
                String.format("json/statistics/orders-total-%s.json", params)
        );
    }

    @ParameterizedTest
    @MethodSource("statisticsParams")
    void getOrdersPurchased(String params) {
        assertResponse(
                get("/statistics/orders/amount/?" + params),
                String.format("json/statistics/orders-amount-%s.json", params)
        );
    }

    @ParameterizedTest
    @MethodSource("statisticsParams")
    void getBooksPurchased(String params) {
        assertResponse(
                get("/statistics/orders/books/?" + params),
                String.format("json/statistics/orders-books-%s.json", params)
        );
    }

    @ParameterizedTest
    @MethodSource("validationParams")
    void getOrdersTotalValidation(String path) {
        assertErrorResponse(get(path),
                null,
                HttpStatus.BAD_REQUEST,
                MethodArgumentTypeMismatchException.class,
                "Parse attempt failed");
    }

}