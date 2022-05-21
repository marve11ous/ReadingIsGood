package com.readingisgood.dao;

import com.readingisgood.dao.dto.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

    List<Order> findAllByStatusInAndDateBetweenOrderByDate(
            Collection<Short> status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Order> findAllByUserIdOrderByDate(Long userId, Pageable pageable);
}
