package com.cafex.pos.controller;

import com.cafex.pos.dto.OwnerDashboardResponse;
import com.cafex.pos.service.OwnerDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
@Slf4j
public class OwnerDashboardController {

    private final OwnerDashboardService ownerDashboardService;

    @GetMapping("/{restaurantId}/owner-dashboard")
    public ResponseEntity<OwnerDashboardResponse> getOwnerDashboard(@PathVariable Long restaurantId) {
        log.info("Get owner dashboard request received - restaurantId: {}", restaurantId);
        OwnerDashboardResponse response = ownerDashboardService.getDashboard(restaurantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config/owner-dashboard")
    public ResponseEntity<Map<String, Object>> getOwnerDashboardConfig() {
        return ResponseEntity.ok(java.util.Map.of(
            "badgeClasses", java.util.Map.of(
                "orderStatus", java.util.List.of(
                    java.util.Map.of("value", "completed", "className", "bg-green-100 dark:bg-green-900/30 text-green-600"),
                    java.util.Map.of("value", "preparing", "className", "bg-orange-100 dark:bg-orange-900/30 text-orange-600"),
                    java.util.Map.of("value", "ready", "className", "bg-blue-100 dark:bg-blue-900/30 text-blue-600"),
                    java.util.Map.of("value", "served", "className", "bg-purple-100 dark:bg-purple-900/30 text-purple-600"),
                    java.util.Map.of("value", "pending", "className", "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600"),
                    java.util.Map.of("value", "cancelled", "className", "bg-red-100 dark:bg-red-900/30 text-red-600"),
                    java.util.Map.of("value", "confirmed", "className", "bg-blue-100 dark:bg-blue-900/30 text-blue-600")
                ),
                "staffStatus", java.util.List.of(
                    java.util.Map.of("value", "active", "className", "bg-green-100 dark:bg-green-900/30 text-green-600"),
                    java.util.Map.of("value", "break", "className", "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600"),
                    java.util.Map.of("value", "offline", "className", "bg-gray-100 dark:bg-gray-700 text-gray-600")
                ),
                "summary", java.util.List.of(
                    java.util.Map.of("value", "excellent", "className", "bg-green-100 dark:bg-green-900/30 text-green-600"),
                    java.util.Map.of("value", "good", "className", "bg-blue-100 dark:bg-blue-900/30 text-blue-600"),
                    java.util.Map.of("value", "average", "className", "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600"),
                    java.util.Map.of("value", "poor", "className", "bg-red-100 dark:bg-red-900/30 text-red-600")
                ),
                "performance", java.util.List.of(
                    java.util.Map.of("value", "95", "className", "bg-green-500"),
                    java.util.Map.of("value", "85", "className", "bg-blue-500"),
                    java.util.Map.of("value", "75", "className", "bg-yellow-500"),
                    java.util.Map.of("value", "default", "className", "bg-red-500")
                ),
                "transactionStatus", java.util.List.of(
                    java.util.Map.of("value", "completed", "className", "bg-green-100 dark:bg-green-900/30 text-green-600"),
                    java.util.Map.of("value", "pending", "className", "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600"),
                    java.util.Map.of("value", "failed", "className", "bg-red-100 dark:bg-red-900/30 text-red-600")
                ),
                "staffPerformance", java.util.List.of(
                    java.util.Map.of("value", "4.5", "className", "bg-green-100 dark:bg-green-900/30 text-green-600"),
                    java.util.Map.of("value", "4.0", "className", "bg-blue-100 dark:bg-blue-900/30 text-blue-600"),
                    java.util.Map.of("value", "3.5", "className", "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600"),
                    java.util.Map.of("value", "default", "className", "bg-red-100 dark:bg-red-900/30 text-red-600")
                ),
                "paymentStatus", java.util.List.of(
                    java.util.Map.of("value", "paid", "className", "bg-green-100 dark:bg-green-900/30 text-green-600"),
                    java.util.Map.of("value", "pending", "className", "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600"),
                    java.util.Map.of("value", "failed", "className", "bg-red-100 dark:bg-red-900/30 text-red-600")
                ),
                "metric", java.util.List.of(
                    java.util.Map.of("value", "excellent", "className", "bg-green-100 dark:bg-green-900/30 text-green-600"),
                    java.util.Map.of("value", "good", "className", "bg-blue-100 dark:bg-blue-900/30 text-blue-600"),
                    java.util.Map.of("value", "average", "className", "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600"),
                    java.util.Map.of("value", "poor", "className", "bg-red-100 dark:bg-red-900/30 text-red-600")
                )
            ),
            "trendIcons", java.util.List.of(
                java.util.Map.of("trend", "up", "iconClass", "fas fa-arrow-up text-green-500"),
                java.util.Map.of("trend", "down", "iconClass", "fas fa-arrow-down text-red-500"),
                java.util.Map.of("trend", "stable", "iconClass", "fas fa-minus text-gray-500")
            ),
            "navigationRoutes", java.util.List.of(
                java.util.Map.of("key", "menu", "route", "/restaurant/menu"),
                java.util.Map.of("key", "staff", "route", "/restaurant/staff"),
                java.util.Map.of("key", "inventory", "route", "/restaurant/inventory"),
                java.util.Map.of("key", "analytics", "route", "/restaurant/analytics")
            ),
            "periodFilterOptions", java.util.List.of(
                java.util.Map.of("value", "today", "label", "Today"),
                java.util.Map.of("value", "week", "label", "This Week"),
                java.util.Map.of("value", "month", "label", "This Month"),
                java.util.Map.of("value", "year", "label", "This Year")
            )
        ));
    }
}
