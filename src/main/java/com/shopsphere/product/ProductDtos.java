package com.shopsphere.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class ProductDtos {
    private ProductDtos() {}
    public record ProductRequest(
            @NotBlank String name,
            @NotBlank String brand,
            @NotBlank String category,
            @NotNull @DecimalMin("0.0") BigDecimal price,
            @NotNull @DecimalMin("0.0") BigDecimal originalPrice,
            @Min(0) int stock,
            @NotBlank String image,
            String description,
            String badge,
            List<String> images,
            Map<String,String> specifications) {}

    public record ProductResponse(
            Long id, String name, String slug, String brand, String category,
            BigDecimal price, BigDecimal originalPrice, double rating, int reviews,
            int stock, String image, List<String> images, String description,
            String badge, Map<String,String> specifications) {}

    public record ProductPage(List<ProductResponse> content, long totalElements, int totalPages, int page, int pageSize) {}
}
