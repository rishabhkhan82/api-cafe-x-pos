package com.cafex.pos.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlatformDashboardResponse {

    private RestaurantStats restaurants;
    private UserStats users;
    private BusinessPulse orders;
    private RevenueStats revenue;
    private SubscriptionStats subscriptions;
    private ChurnStats churn;

    public PlatformDashboardResponse() {}

    public PlatformDashboardResponse(RestaurantStats restaurants,
                                     UserStats users,
                                     BusinessPulse orders,
                                     RevenueStats revenue,
                                     SubscriptionStats subscriptions,
                                     ChurnStats churn) {
        this.restaurants = restaurants;
        this.users = users;
        this.orders = orders;
        this.revenue = revenue;
        this.subscriptions = subscriptions;
        this.churn = churn;
    }

    @Data
    public static class RestaurantStats {
        private long total;
        private long newToday;
        private long newThisWeek;
        private long active;
        private long trial;
        private long expired;
        private long suspended;
    }

    @Data
    public static class UserStats {
        private long platformOwners;
        private long restaurantOwners;
        private long restaurantManagers;
        private long kitchenManagers;
        private long waiters;
        private long cashiers;
        private long endCustomers;
        private long newUsersToday;
        private long newCustomersToday;
        private long newThisWeek;
    }

    @Data
    public static class BusinessPulse {
        private long ordersToday;
        private long uncompletedOrdersToday;
        private long completedOrdersToday;
        private BigDecimal ordersTodayAmount;
        private long totalOrders;
        private BigDecimal totalOrderAmount;
    }

    @Data
    public static class RevenueStats {
        private BigDecimal currentMonth;
        private BigDecimal total;
        private double growth;
        private List<BigDecimal> monthly;
    }

    @Data
    public static class SubscriptionStats {
        private List<String> labels;
        private List<Long> data;
    }

    @Data
    public static class ChurnStats {
        private double rate;
        private double trend;
    }
}
