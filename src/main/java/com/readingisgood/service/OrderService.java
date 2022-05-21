package com.readingisgood.service;

import com.readingisgood.models.common.OrderStatus;
import com.readingisgood.models.request.CreateOrderRequest;
import com.readingisgood.models.response.OrderResponse;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OrderService {

    OrderResponse saveOrder(CreateOrderRequest request);

    OrderResponse getOrder(Long id);

    List<OrderResponse> getOrders(Collection<OrderStatus> statuses,
                                  LocalDateTime startDate, LocalDateTime endDate,
                                  int page, int size);

    List<OrderResponse> getOrders(Long userId, int page, int size);
}
