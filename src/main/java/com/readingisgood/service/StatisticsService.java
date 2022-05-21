package com.readingisgood.service;

import com.readingisgood.models.common.OrderStatus;

import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

public interface StatisticsService {

    Map<Integer, Map<Month, Map<OrderStatus, Long>>> getOrdersCount(YearMonth startMonth, YearMonth endMonth);

    Map<Integer, Map<Month, Map<OrderStatus, Double>>> getPurchasedAmount(YearMonth startMonth, YearMonth endMonth);

    Map<Integer, Map<Month, Map<OrderStatus, Long>>> getPurchasedBooksCount(YearMonth startMonth, YearMonth endMonth);
}
