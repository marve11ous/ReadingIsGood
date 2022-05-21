package com.readingisgood.controller;

import com.readingisgood.models.common.OrderStatus;
import com.readingisgood.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Statistics API")
@RequestMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatisticsController implements SecuredController {
    private static final String DEFAULT_MONTH = "#{T(java.time.YearMonth).now()}";
    private static final String MONTH_PATTERN = "yyyy-MM";

    private final StatisticsService statisticsService;

    @GetMapping(value = "/orders/total/")
    @Operation(summary = "Monthly order count for specified time period")
    public Map<Integer, Map<Month, Map<OrderStatus, Long>>> getOrdersTotal(
            @DateTimeFormat(pattern = MONTH_PATTERN)
            @RequestParam(defaultValue = DEFAULT_MONTH) YearMonth startMonth,
            @DateTimeFormat(pattern = MONTH_PATTERN)
            @RequestParam(defaultValue = DEFAULT_MONTH) YearMonth endMonth
    ) {
        return statisticsService.getOrdersCount(startMonth, endMonth);
    }

    @GetMapping(value = "/orders/amount/")
    @Operation(summary = "Monthly purchased amount for specified time period")
    public Map<Integer, Map<Month, Map<OrderStatus, Double>>> getOrdersPurchased(
            @DateTimeFormat(pattern = MONTH_PATTERN)
            @RequestParam(defaultValue = DEFAULT_MONTH) YearMonth startMonth,
            @DateTimeFormat(pattern = MONTH_PATTERN)
            @RequestParam(defaultValue = DEFAULT_MONTH) YearMonth endMonth
    ) {
        return statisticsService.getPurchasedAmount(startMonth, endMonth);
    }

    @GetMapping(value = "/orders/books/")
    @Operation(summary = "Monthly purchased books count for specified time period")
    public Map<Integer, Map<Month, Map<OrderStatus, Long>>> getBooksPurchased(
            @DateTimeFormat(pattern = MONTH_PATTERN)
            @RequestParam(defaultValue = DEFAULT_MONTH) YearMonth startMonth,
            @DateTimeFormat(pattern = MONTH_PATTERN)
            @RequestParam(defaultValue = DEFAULT_MONTH) YearMonth endMonth
    ) {
        return statisticsService.getPurchasedBooksCount(startMonth, endMonth);
    }
}
