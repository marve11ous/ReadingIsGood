package com.readingisgood.controller;

import com.readingisgood.models.request.CreateUserRequest;
import com.readingisgood.models.response.OrderResponse;
import com.readingisgood.models.response.UserResponse;
import com.readingisgood.service.OrderService;
import com.readingisgood.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Customer management API")
@RequestMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController implements SecuredController {
    private final UserService userService;
    private final OrderService orderService;

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new customer")
    public UserResponse createCustomer(@Valid @RequestBody CreateUserRequest request) {
        return userService.saveUser(request);
    }

    @GetMapping(value = "/")
    @Operation(summary = "Get all customers")
    public List<UserResponse> getCustomers() {
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}/orders/")
    @Operation(summary = "Get all customer orders (by pages)")
    public List<OrderResponse> getCustomerOrders(@PathVariable Long id,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "100") int size) {
        return orderService.getOrders(id, page, size);
    }

}
