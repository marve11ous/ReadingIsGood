package com.readingisgood.dao;

import com.readingisgood.dao.dto.MonthlyStat;
import com.readingisgood.dao.dto.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Repository
public interface StatisticsRepository extends Repository<Order, Long> {

    @Query(value = "select new com.readingisgood.dao.dto.MonthlyStat(year(O.date), month(O.date)," +
            " O.status, coalesce(sum(elements(B)), 0))" +
            " from Order O join O.books B" +
            " where O.date between ?1 and ?2" +
            " group by year(O.date), month(O.date), O.status" +
            " order by year(O.date), month(O.date), O.status")
    List<MonthlyStat> countPurchasedBooksByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "select new com.readingisgood.dao.dto.MonthlyStat(year(O.date), month(O.date)," +
            " O.status, coalesce(sum(B1.price * B), 0))" +
            " from Order O join O.books B join Book B1 on B1.id = index(B)" +
            " where O.date between ?1 and ?2" +
            " group by year(O.date), month(O.date), O.status" +
            " order by year(O.date), month(O.date), O.status")
    List<MonthlyStat> countPurchasedAmountByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "select new com.readingisgood.dao.dto.MonthlyStat(year(O.date), month(O.date)," +
            " O.status, coalesce(count(O), 0))" +
            " from Order O" +
            " where O.date between ?1 and ?2" +
            " group by year(O.date), month(O.date), O.status" +
            " order by year(O.date), month(O.date), O.status")
    List<MonthlyStat> countByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
