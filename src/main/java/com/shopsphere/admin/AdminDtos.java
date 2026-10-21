package com.shopsphere.admin;

import java.math.BigDecimal;
import java.util.List;

public final class AdminDtos {
    private AdminDtos() {}
    public record InventoryItem(Long id, String name, String image, String category, int stock, BigDecimal price) {}
    public record UserItem(Long id, String name, String email, String role) {}
    public record AnalyticsResponse(BigDecimal grossSales, long orders, long customers, long lowStockProducts, List<MonthlyMetric> monthly, List<DailyMetric> daily) {}
    public record MonthlyMetric(String month, BigDecimal revenue, long orders) {}
    public record DailyMetric(String day, BigDecimal sales) {}
}
