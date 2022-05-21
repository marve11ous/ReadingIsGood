package com.readingisgood.service;

import com.readingisgood.dao.OrderRepository;
import com.readingisgood.dao.dto.Book;
import com.readingisgood.dao.dto.Order;
import com.readingisgood.exceptions.OrderNotFoundException;
import com.readingisgood.mapper.OrderMapper;
import com.readingisgood.models.common.OrderStatus;
import com.readingisgood.models.request.BookOrderRequest;
import com.readingisgood.models.request.CreateOrderRequest;
import com.readingisgood.models.request.UpdateBookRequest;
import com.readingisgood.models.response.BookOrderResponse;
import com.readingisgood.models.response.OrderResponse;
import com.readingisgood.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Spy
    @SuppressWarnings("unused")
    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BookService bookService;

    @Test
    void saveOrderOk() {
        var request = new CreateOrderRequest(1L,
                List.of(
                        new BookOrderRequest(1L, 2L),
                        new BookOrderRequest(2L, 4L),
                        new BookOrderRequest(3L, 6L)
                ));

        doReturn(Map.of(1L, 1L, 2L, 3L, 3L, 0L))
                .when(bookService).lockAvailableBooks(eq(request.getBooks()));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(orderRepository).save(any(Order.class));

        var orderResponse = orderService.saveOrder(request);
        assertEquals(OrderStatus.OK, orderResponse.getStatus());
        assertThat(orderResponse.getBooks(), containsInAnyOrder(
                new BookOrderResponse(1L, 2L, BookOrderResponse.Status.OK),
                new BookOrderResponse(2L, 4L, BookOrderResponse.Status.OK),
                new BookOrderResponse(3L, 6L, BookOrderResponse.Status.OK)
        ));
        verify(bookService).updateBook(eq(1L), eq(new UpdateBookRequest(1L)));
        verify(bookService).updateBook(eq(2L), eq(new UpdateBookRequest(3L)));
        verify(bookService).updateBook(eq(3L), eq(new UpdateBookRequest(0L)));
    }

    @Test
    void saveOrderFailed() {
        var request = new CreateOrderRequest(1L,
                List.of(
                        new BookOrderRequest(1L, 2L),
                        new BookOrderRequest(2L, 6L)
                ));

        doReturn(Map.of(1L, -1L, 2L, 0L))
                .when(bookService).lockAvailableBooks(eq(request.getBooks()));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(orderRepository).save(any(Order.class));

        var orderResponse = orderService.saveOrder(request);
        assertEquals(OrderStatus.FAILED, orderResponse.getStatus());
        assertThat(orderResponse.getBooks(), containsInAnyOrder(
                new BookOrderResponse(1L, 2L, BookOrderResponse.Status.OUT_OF_STOCK),
                new BookOrderResponse(2L, 6L, BookOrderResponse.Status.OK)
        ));
        verify(bookService, never()).updateBook(anyLong(), any(UpdateBookRequest.class));
    }

    @Test
    void getOrderOk() {
        var order = new Order();
        order.setId(1L);
        order.setStatus((short) 0);
        order.setDate(LocalDateTime.MIN);
        order.setBooks(Map.of(new Book(), 12L));
        order.getBooks().keySet().iterator().next().setId(2L);
        doReturn(Optional.of(order)).when(orderRepository).findById(eq(1L));
        var orderResponse = orderService.getOrder(1L);
        assertEquals(order.getId(), orderResponse.getId());
        assertEquals(OrderStatus.OK, orderResponse.getStatus());
        assertEquals(List.of(new BookOrderResponse(2L, 12L, null)), orderResponse.getBooks());
    }

    @Test
    void getOrderFailed() {
        doReturn(Optional.empty()).when(orderRepository).findById(eq(1L));
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(1L));
    }

    @Test
    void getOrdersBetween() {
        var startDate = LocalDateTime.MIN;
        var endDate = LocalDateTime.MIN.plusMonths(1);
        var orders = List.of(new Order(), new Order());
        orders.get(0).setId(1L);
        orders.get(0).setStatus((short) 0);
        orders.get(0).setDate(startDate);
        orders.get(0).setBooks(Map.of(new Book(), 12L));
        orders.get(0).getBooks().keySet().iterator().next().setId(2L);
        orders.get(1).setId(2L);
        orders.get(1).setStatus((short) 1);
        orders.get(1).setDate(endDate);
        orders.get(1).setBooks(Map.of(new Book(), 10L));
        orders.get(1).getBooks().keySet().iterator().next().setId(3L);
        doReturn(orders).when(orderRepository).findAllByStatusInAndDateBetweenOrderByDate(
                eq(List.of((short) 0, (short) 1)), eq(startDate), eq(endDate), eq(PageRequest.of(1, 2)));

        assertThat(orderService.getOrders(List.of(OrderStatus.OK, OrderStatus.FAILED), startDate, endDate, 1, 2),
                containsInAnyOrder(
                        new OrderResponse(1L, OrderStatus.OK, startDate,
                                List.of(new BookOrderResponse(2L, 12L, null))),
                        new OrderResponse(2L, OrderStatus.FAILED, endDate,
                                List.of(new BookOrderResponse(3L, 10L, null)))
                ));
    }

    @Test
    void getOrdersByUser() {
        var orders = List.of(new Order(), new Order());
        orders.get(0).setId(1L);
        orders.get(0).setStatus((short) 0);
        orders.get(0).setDate(LocalDateTime.MIN);
        orders.get(0).setBooks(Map.of(new Book(), 12L));
        orders.get(0).getBooks().keySet().iterator().next().setId(2L);
        orders.get(1).setId(2L);
        orders.get(1).setStatus((short) 1);
        orders.get(1).setDate(LocalDateTime.MIN.plusMonths(1));
        orders.get(1).setBooks(Map.of(new Book(), 10L));
        orders.get(1).getBooks().keySet().iterator().next().setId(3L);
        doReturn(List.of(orders.get(1))).when(orderRepository)
                .findAllByUserIdOrderByDate(eq(1L), eq(PageRequest.of(2, 3)));
        doReturn(orders).when(orderRepository)
                .findAllByUserIdOrderByDate(eq(1L), eq(PageRequest.of(0, 10)));

        assertEquals(List.of(new OrderResponse(2L, OrderStatus.FAILED, LocalDateTime.MIN.plusMonths(1),
                        List.of(new BookOrderResponse(3L, 10L, null)))),
                orderService.getOrders(1L, 2, 3));

        assertThat(orderService.getOrders(1L, 0, 10), containsInAnyOrder(
                new OrderResponse(1L, OrderStatus.OK, LocalDateTime.MIN,
                        List.of(new BookOrderResponse(2L, 12L, null))),
                new OrderResponse(2L, OrderStatus.FAILED, LocalDateTime.MIN.plusMonths(1),
                        List.of(new BookOrderResponse(3L, 10L, null)))
        ));
    }
}