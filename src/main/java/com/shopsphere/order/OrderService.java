
package com.shopsphere.order;

import com.shopsphere.product.Product;
import com.shopsphere.product.ProductService;
import com.shopsphere.user.User;
import com.shopsphere.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductService productService
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    @Transactional
    public OrderDtos.OrderResponse create(
            String email,
            OrderDtos.CreateOrderRequest request
    ) {
        User user = getUser(email);

        Order order = new Order();
        order.setId("SS-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase(Locale.ROOT));
        order.setUser(user);
        order.setCreatedAt(Instant.now());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentMethod(request.paymentMethod());

        order.setFullName(request.address().fullName());
        order.setPhone(request.address().phone());
        order.setLine1(request.address().line1());
        order.setCity(request.address().city());
        order.setState(request.address().state());
        order.setPostalCode(request.address().postalCode());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderDtos.OrderItemRequest itemRequest : request.items()) {
            Product product = productService.getEntity(itemRequest.productId());

            if (product.getStock() < itemRequest.quantity()) {
                throw new IllegalArgumentException(
                        "Not enough stock for " + product.getName()
                );
            }

            product.setStock(product.getStock() - itemRequest.quantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setBrand(product.getBrand());
            item.setImage(product.getImage());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice());

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity()));

            item.setLineTotal(lineTotal);
            subtotal = subtotal.add(lineTotal);
            order.getItems().add(item);
        }

        BigDecimal shipping =
                subtotal.compareTo(BigDecimal.ZERO) == 0
                        || subtotal.compareTo(BigDecimal.valueOf(4999)) >= 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(99);

        BigDecimal discount =
                subtotal.compareTo(BigDecimal.valueOf(25000)) >= 0
                        ? subtotal.multiply(new BigDecimal("0.05"))
                        .setScale(0, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

        BigDecimal total = subtotal.add(shipping).subtract(discount);

        order.setSubtotal(subtotal);
        order.setShipping(shipping);
        order.setDiscount(discount);
        order.setTotal(total);

        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderDtos.OrderResponse> myOrders(String email) {
        return orderRepository
                .findByUserOrderByCreatedAtDesc(getUser(email))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDtos.OrderResponse get(
            String email,
            String id,
            boolean admin
    ) {
        Order order = admin
                ? orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."))
                : orderRepository.findByIdAndUser(id, getUser(email))
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDtos.OrderResponse> allOrders() {
        return orderRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderDtos.OrderResponse updateStatus(String id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase(Locale.ROOT)));

        return toResponse(orderRepository.save(order));
    }

    private User getUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private OrderDtos.OrderResponse toResponse(Order order) {
        List<OrderDtos.OrderItemResponse> items = order.getItems()
                .stream()
                .map(i -> {
                    Product p = productService.getEntity(i.getProductId());

                    List<String> images = p.getImages() == null
                            ? List.of()
                            : new java.util.ArrayList<>(p.getImages());

                    java.util.Map<String, String> specifications =
                            p.getSpecifications() == null
                                    ? java.util.Map.of()
                                    : new java.util.HashMap<>(p.getSpecifications());

                    var snapshot = new OrderDtos.ProductSnapshot(
                            p.getId(),
                            p.getName(),
                            p.getSlug(),
                            p.getBrand(),
                            p.getCategory(),
                            p.getPrice(),
                            p.getOriginalPrice(),
                            p.getRating(),
                            p.getReviews(),
                            p.getStock(),
                            p.getImage(),
                            images,
                            p.getDescription(),
                            p.getBadge(),
                            specifications
                    );

                    return new OrderDtos.OrderItemResponse(
                            snapshot,
                            i.getQuantity(),
                            i.getLineTotal()
                    );
                })
                .toList();

        return new OrderDtos.OrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus().name(),
                order.getSubtotal(),
                order.getShipping(),
                order.getDiscount(),
                order.getTotal(),
                items,
                new OrderDtos.AddressResponse(
                        order.getFullName(),
                        order.getPhone(),
                        order.getLine1(),
                        order.getCity(),
                        order.getState(),
                        order.getPostalCode()
                ),
                order.getPaymentMethod()
        );
    }
}