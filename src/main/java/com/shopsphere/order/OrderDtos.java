package com.shopsphere.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {
    private OrderDtos() {}
    public record AddressRequest(@NotBlank String fullName, @NotBlank String phone, @NotBlank String line1,
                                 @NotBlank String city, @NotBlank String state, @NotBlank String postalCode) {}
    public record OrderItemRequest(@Min(1) Long productId, @Min(1) int quantity) {}
    public record CreateOrderRequest(@Valid AddressRequest address, @NotBlank String paymentMethod,
                                     @NotEmpty List<@Valid OrderItemRequest> items) {}
    public record AddressResponse(String fullName, String phone, String line1, String city, String state, String postalCode) {}
    public record OrderItemResponse(ProductSnapshot product, int quantity, BigDecimal price) {}
    public record ProductSnapshot(Long id, String name, String slug, String brand, String category, BigDecimal price,
                                  BigDecimal originalPrice, double rating, int reviews, int stock, String image,
                                  List<String> images, String description, String badge, java.util.Map<String,String> specifications) {}
//    public record OrderResponse(String id, Instant createdAt, String status, BigDecimal total, List<OrderItemResponse> items,
//                                AddressResponse address, String paymentMethod) {}


    public record OrderResponse(
            String id,
            Instant createdAt,
            String status,
            BigDecimal subtotal,
            BigDecimal shipping,
            BigDecimal discount,
            BigDecimal total,
            List<OrderItemResponse> items,
            AddressResponse address,
            String paymentMethod
    ) {}
    public record UpdateStatusRequest(@NotBlank String status) {}
}
