package com.shopsphere.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderDtos.OrderResponse create(Authentication auth, @Valid @RequestBody OrderDtos.CreateOrderRequest request) { return service.create(auth.getName(), request); }
    @GetMapping("/me") public List<OrderDtos.OrderResponse> mine(Authentication auth) { return service.myOrders(auth.getName()); }
    @GetMapping("/{id}") public OrderDtos.OrderResponse get(Authentication auth, @PathVariable String id) { return service.get(auth.getName(), id, auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))); }
}
