package com.readingisgood.service;

import com.readingisgood.dao.StatisticsRepository;
import com.readingisgood.dao.dto.MonthlyStat;
import com.readingisgood.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static com.readingisgood.models.common.OrderStatus.FAILED;
import static com.readingisgood.models.common.OrderStatus.OK;
import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.MAY;
import static java.time.Month.SEPTEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private StatisticsRepository statisticsRepository;

    private static LocalDateTime getMonthDate(YearMonth yearMonth) {
        return LocalDateTime.of(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1), LocalTime.MIDNIGHT);
    }

    @Test
    void getOrdersCount() {
        var startMonth = YearMonth.of(2022, Month.MARCH);
        var endMonth = YearMonth.of(2022, Month.JUNE);
        doReturn(List.of(
                new MonthlyStat(2021, APRIL.getValue(), (short) 0, 12L),
                new MonthlyStat(2021, MAY.getValue(), (short) 0, 10L),
                new MonthlyStat(2022, APRIL.getValue(), (short) 0, 12L)
        )).when(statisticsRepository).countByDateBetween(
                eq(getMonthDate(startMonth)), eq(getMonthDate(endMonth).plusMonths(1)));
        assertEquals(
                Map.of(
                        2021, Map.of(APRIL, Map.of(OK, 12L), MAY, Map.of(OK, 10L)),
                        2022, Map.of(APRIL, Map.of(OK, 12L))
                ),
                statisticsService.getOrdersCount(startMonth, endMonth));
    }

    @Test
    void getPurchasedAmount() {
        var startMonth = YearMonth.of(2022, Month.MARCH);
        var endMonth = YearMonth.of(2022, Month.JUNE);
        doReturn(List.of(
                new MonthlyStat(2021, SEPTEMBER.getValue(), (short) 0, 120.11D),
                new MonthlyStat(2021, SEPTEMBER.getValue(), (short) 1, 10.99D),
                new MonthlyStat(2022, APRIL.getValue(), (short) 0, 120.11D),
                new MonthlyStat(2022, MAY.getValue(), (short) 0, 10.99D)
        )).when(statisticsRepository).countPurchasedAmountByDateBetween(
                eq(getMonthDate(startMonth)), eq(getMonthDate(endMonth).plusMonths(1)));
        assertEquals(
                Map.of(
                        2021, Map.of(SEPTEMBER, Map.of(OK, 120.11, FAILED, 10.99)),
                        2022, Map.of(APRIL, Map.of(OK, 120.11), MAY, Map.of(OK, 10.99))
                ),
                statisticsService.getPurchasedAmount(startMonth, endMonth));
    }

    @Test
    void getPurchasedBooksCount() {
        var startMonth = YearMonth.of(2022, Month.MARCH);
        var endMonth = YearMonth.of(2022, Month.JUNE);
        doReturn(List.of(
                new MonthlyStat(2021, AUGUST.getValue(), (short) 1, 3L),
                new MonthlyStat(2022, APRIL.getValue(), (short) 1, 2L),
                new MonthlyStat(2022, APRIL.getValue(), (short) 0, 12L),
                new MonthlyStat(2022, MAY.getValue(), (short) 0, 10L)
        )).when(statisticsRepository).countPurchasedBooksByDateBetween(
                eq(getMonthDate(startMonth)), eq(getMonthDate(endMonth).plusMonths(1)));
        assertEquals(
                Map.of(
                        2021, Map.of(AUGUST, Map.of(FAILED, 3L)),
                        2022, Map.of(APRIL, Map.of(OK, 12L, FAILED, 2L), MAY, Map.of(OK, 10L))
                ),
                statisticsService.getPurchasedBooksCount(startMonth, endMonth));
    }
}