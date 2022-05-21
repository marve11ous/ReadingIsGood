package com.readingisgood.mapper;

import com.readingisgood.dao.dto.Book;
import com.readingisgood.dao.dto.Order;
import com.readingisgood.models.common.OrderStatus;
import com.readingisgood.models.request.BookOrderRequest;
import com.readingisgood.models.request.CreateOrderRequest;
import com.readingisgood.models.response.BookOrderResponse;
import com.readingisgood.models.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface OrderMapper {

    @Mapping(target = "user.id", source = "request.userId")
    @Mapping(target = "status", source = "status.code")
    Order toOrder(CreateOrderRequest request, OrderStatus status);

    OrderResponse toOrderResponse(Order order);

    default OrderStatus toOrderStatus(Short value) {
        return OrderStatus.fromCode(value);
    }

    Book mapBookOrderRequest(BookOrderRequest request);

    default Map<Book, Long> mapBookOrderRequests(List<BookOrderRequest> requests) {
        return requests.stream().collect(Collectors.toMap(this::mapBookOrderRequest, BookOrderRequest::getAmount));
    }

    default List<BookOrderResponse> mapBookOrderResponse(Map<Book, Long> value) {
        return value.entrySet().stream()
                .map(e -> {
                    var response = new BookOrderResponse();
                    response.setId(e.getKey().getId());
                    response.setAmount(e.getValue());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
