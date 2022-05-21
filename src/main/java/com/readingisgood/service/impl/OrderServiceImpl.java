package com.readingisgood.service.impl;

import com.readingisgood.dao.OrderRepository;
import com.readingisgood.exceptions.OrderNotFoundException;
import com.readingisgood.mapper.OrderMapper;
import com.readingisgood.models.common.OrderStatus;
import com.readingisgood.models.request.BookOrderRequest;
import com.readingisgood.models.request.CreateOrderRequest;
import com.readingisgood.models.request.UpdateBookRequest;
import com.readingisgood.models.response.BookOrderResponse;
import com.readingisgood.models.response.OrderResponse;
import com.readingisgood.service.BookService;
import com.readingisgood.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final BookService bookService;

    private static BookOrderResponse.Status getStatus(Long stock) {
        if (stock < 0) {
            return BookOrderResponse.Status.OUT_OF_STOCK;
        }
        return BookOrderResponse.Status.OK;
    }

    @Override
    @Transactional
    public OrderResponse saveOrder(CreateOrderRequest request) {
        var availableBooks = bookService.lockAvailableBooks(request.getBooks());
        var orderStatus = request.getBooks().stream().map(BookOrderRequest::getId)
                .map(availableBooks::get).allMatch(stock -> stock != null && stock >= 0)
                ? OrderStatus.OK : OrderStatus.FAILED;
        var response = mapper.toOrderResponse(repository.save(mapper.toOrder(request, orderStatus)));
        if (orderStatus == OrderStatus.OK) {
            availableBooks.forEach((bookId, stock) -> bookService.updateBook(bookId, new UpdateBookRequest(stock)));
        }
        response.getBooks().forEach(book -> book.setStatus(getStatus(availableBooks.get(book.getId()))));
        log.info("New order placed {}", response);
        return response;
    }

    @Override
    public OrderResponse getOrder(Long id) {
        return repository.findById(id).map(mapper::toOrderResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public List<OrderResponse> getOrders(Collection<OrderStatus> statuses,
                                         LocalDateTime startDate, LocalDateTime endDate,
                                         int page, int size) {
        var statusCodes = statuses.stream().map(OrderStatus::getCode).toList();
        return repository
                .findAllByStatusInAndDateBetweenOrderByDate(statusCodes, startDate, endDate, PageRequest.of(page, size))
                .stream().map(mapper::toOrderResponse).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrders(Long userId, int page, int size) {
        return repository.findAllByUserIdOrderByDate(userId, PageRequest.of(page, size)).stream()
                .map(mapper::toOrderResponse).collect(Collectors.toList());
    }

}
