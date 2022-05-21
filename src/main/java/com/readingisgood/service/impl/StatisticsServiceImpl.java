package com.readingisgood.service.impl;

import com.readingisgood.dao.StatisticsRepository;
import com.readingisgood.dao.dto.MonthlyStat;
import com.readingisgood.models.common.OrderStatus;
import com.readingisgood.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;

    private static LocalDateTime getMonthDate(YearMonth yearMonth) {
        return LocalDateTime.of(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1), LocalTime.MIDNIGHT);
    }

    private static <N extends Number> Map<Month, Map<OrderStatus, N>>
    groupByMonth(List<MonthlyStat> stats, Function<Number, N> mapper) {
        return stats.stream().collect(Collectors.groupingBy(MonthlyStat::getMonth))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        stat -> Month.of(stat.getKey()),
                        stat -> collectStatusToCount(stat.getValue(), mapper)
                ));
    }

    private static <N extends Number> Map<OrderStatus, N>
    collectStatusToCount(List<MonthlyStat> stats, Function<Number, N> mapper) {
        return stats.stream().collect(Collectors.toMap(
                monthlyStat -> OrderStatus.fromCode(monthlyStat.getStatus()),
                monthlyStat -> mapper.apply(monthlyStat.getCount())
        ));
    }

    private <N extends Number> Map<Integer, Map<Month, Map<OrderStatus, N>>>
    getStats(YearMonth startMonth,
             YearMonth endMonth,
             BiFunction<LocalDateTime, LocalDateTime, List<MonthlyStat>> provider,
             Function<Number, N> mapper) {
        return provider.apply(getMonthDate(startMonth), getMonthDate(endMonth).plusMonths(1))
                .stream().collect(Collectors.groupingBy(MonthlyStat::getYear))
                .entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> groupByMonth(e.getValue(), mapper)
                ));
    }

    @Override
    public Map<Integer, Map<Month, Map<OrderStatus, Long>>> getOrdersCount(YearMonth startMonth, YearMonth endMonth) {
        return getStats(startMonth, endMonth, statisticsRepository::countByDateBetween, Number::longValue);
    }

    @Override
    public Map<Integer, Map<Month, Map<OrderStatus, Double>>> getPurchasedAmount(YearMonth startMonth, YearMonth endMonth) {
        return getStats(startMonth, endMonth, statisticsRepository::countPurchasedAmountByDateBetween, Number::doubleValue);
    }

    @Override
    public Map<Integer, Map<Month, Map<OrderStatus, Long>>> getPurchasedBooksCount(YearMonth startMonth, YearMonth endMonth) {
        return getStats(startMonth, endMonth, statisticsRepository::countPurchasedBooksByDateBetween, Number::longValue);
    }
}
