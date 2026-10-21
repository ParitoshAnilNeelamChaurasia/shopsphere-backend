package com.shopsphere.admin;

import com.shopsphere.auth.AuthService;
import com.shopsphere.order.OrderDtos;
import com.shopsphere.order.OrderRepository;
import com.shopsphere.order.OrderService;
import com.shopsphere.product.ProductRepository;
import com.shopsphere.product.ProductService;
import com.shopsphere.user.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ProductRepository productRepository; private final UserRepository userRepository; private final ProductService productService; private final OrderService orderService; private final OrderRepository orderRepository; private final AuthService authService;
    public AdminController(ProductRepository productRepository, UserRepository userRepository, ProductService productService, OrderService orderService, OrderRepository orderRepository, AuthService authService) {
        this.productRepository=productRepository; this.userRepository=userRepository; this.productService=productService; this.orderService=orderService; this.orderRepository=orderRepository; this.authService=authService;
    }
    @GetMapping("/inventory") public List<AdminDtos.InventoryItem> inventory() { return productRepository.findAll().stream().map(p -> new AdminDtos.InventoryItem(p.getId(), p.getName(), p.getImage(), p.getCategory(), p.getStock(), p.getPrice())).toList(); }
    public record StockUpdate(int stock) {}
    @PutMapping("/inventory/{productId}") public AdminDtos.InventoryItem updateStock(@PathVariable Long productId, @RequestBody StockUpdate request) {
        var p=productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found.")); p.setStock(Math.max(0, request.stock())); productRepository.save(p); return new AdminDtos.InventoryItem(p.getId(), p.getName(), p.getImage(), p.getCategory(), p.getStock(), p.getPrice());
    }
    @GetMapping("/orders") public List<OrderDtos.OrderResponse> orders() { return orderService.allOrders(); }
    @PutMapping("/orders/{id}/status") public OrderDtos.OrderResponse status(@PathVariable String id, @RequestBody OrderDtos.UpdateStatusRequest request) { return orderService.updateStatus(id, request.status()); }
    @GetMapping("/users") public List<AdminDtos.UserItem> users() { return userRepository.findAll().stream().map(u -> new AdminDtos.UserItem(u.getId(),u.getName(),u.getEmail(),u.getRole().name())).toList(); }
    @GetMapping("/analytics") public AdminDtos.AnalyticsResponse analytics() {
        var orders=orderRepository.findAll(); var gross=orders.stream().map(o->o.getTotal()).reduce(BigDecimal.ZERO,BigDecimal::add); long low=productRepository.findAll().stream().filter(p->p.getStock()<15).count();
        var months = new ArrayList<AdminDtos.MonthlyMetric>(); for (int m=1;m<=6;m++) { final int month=m; var matching=orders.stream().filter(o->o.getCreatedAt().atZone(java.time.ZoneId.of("Asia/Kolkata")).getMonthValue()==month).toList(); var rev=matching.stream().map(o->o.getTotal()).reduce(BigDecimal.ZERO,BigDecimal::add); months.add(new AdminDtos.MonthlyMetric(Month.of(month).name().substring(0,1)+Month.of(month).name().substring(1).toLowerCase(), rev, matching.size())); }
        var daily=List.of(new AdminDtos.DailyMetric("Mon",BigDecimal.ZERO),new AdminDtos.DailyMetric("Tue",BigDecimal.ZERO),new AdminDtos.DailyMetric("Wed",BigDecimal.ZERO),new AdminDtos.DailyMetric("Thu",BigDecimal.ZERO),new AdminDtos.DailyMetric("Fri",BigDecimal.ZERO),new AdminDtos.DailyMetric("Sat",BigDecimal.ZERO),new AdminDtos.DailyMetric("Sun",BigDecimal.ZERO));
        return new AdminDtos.AnalyticsResponse(gross, orders.size(), userRepository.count(), low, months, daily);
    }
}
