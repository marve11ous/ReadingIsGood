package com.readingisgood.controller;

import com.readingisgood.models.common.OrderStatus;
import com.readingisgood.models.request.CreateOrderRequest;
import com.readingisgood.models.response.OrderResponse;
import com.readingisgood.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.AbstractThrowableProblem;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Order management API")
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController implements SecuredController {
    private static final String DEFAULT_STATUS = "OK,FAILED";
    private static final String DEFAULT_DATE_TIME = "#{T(java.time.LocalDateTime).now()}";

    private final OrderService orderService;

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new order")
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.saveOrder(request);
    }

    @GetMapping(value = "/{id}/")
    @Operation(summary = "Get order info")
    @ApiResponse(responseCode = "404",
            content = @Content(schema = @Schema(implementation = AbstractThrowableProblem.class)))
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @GetMapping(value = "/")
    @Operation(summary = "Get all orders for specified time period and statuses (by pages)")
    public List<OrderResponse> getOrders(
            @RequestParam(defaultValue = DEFAULT_STATUS) Collection<OrderStatus> status,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam LocalDateTime startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(defaultValue = DEFAULT_DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return orderService.getOrders(status, startDate, endDate, page, size);
    }

}
