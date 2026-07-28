package com.cafex.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDashboardResponse {

    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalCustomers;
    private BigDecimal avgOrderValue;

    private BigDecimal todayRevenue;
    private long todayOrders;
    private long todayCustomers;
    private BigDecimal todayAvgOrderValue;

    private List<RecentOrder> recentOrders;

    private long activeStaff;
    private long totalStaff;
    private long staffOnBreak;
    private List<StaffPerformance> staffPerformance;

    private List<InventoryAlert> lowStockItems;
    private List<InventoryAlert> outOfStockItems;

    private List<PopularItem> popularItems;

    private long pendingOrders;
    private long completedOrders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentOrder {
        private String id;
        private String customer;
        private int items;
        private BigDecimal amount;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffPerformance {
        private String name;
        private String role;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryAlert {
        private String name;
        private BigDecimal current;
        private BigDecimal minimum;
        private String unit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularItem {
        private String name;
        private int orders;
        private BigDecimal revenue;
        private String trend;
    }
}
