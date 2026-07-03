package com.cafex.pos.service;

import com.cafex.pos.dto.OwnerDashboardResponse;
import com.cafex.pos.entity.InventoryItem;
import com.cafex.pos.entity.Order;
import com.cafex.pos.repository.InventoryItemRepository;
import com.cafex.pos.repository.OrderItemRepository;
import com.cafex.pos.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerDashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String TOPIC_PREFIX = "/topic/restaurant/";
    private static final long THROTTLE_MILLIS = 10_000;

    private final Map<Long, Long> lastEmitByRestaurant = new ConcurrentHashMap<>();

    public OwnerDashboardResponse getDashboard(Long restaurantId) {
        if (restaurantId == null) {
            return new OwnerDashboardResponse();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.with(LocalTime.MIN);
        LocalDateTime weekStart = now.minusDays(7).with(LocalTime.MIN);

        // Order metrics
        long totalOrders = orderRepository.countByRestaurantId(restaurantId);
        long completedOrders = orderRepository.countByRestaurantIdAndStatus(restaurantId, Order.OrderStatus.COMPLETED);
        long pendingOrders = totalOrders - completedOrders;
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByRestaurantIdAndStatus(restaurantId, Order.OrderStatus.COMPLETED);

        BigDecimal avgOrderValue = totalOrders > 0 ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;

        long todayOrders = orderRepository.countByRestaurantIdAndCreatedAtAfter(restaurantId, todayStart);
        long todayCompleted = orderRepository.countByRestaurantIdAndStatusAndCreatedAtAfter(restaurantId, Order.OrderStatus.COMPLETED, todayStart);
        BigDecimal todayRevenue = orderRepository.sumTotalAmountByRestaurantIdAndStatusAndCreatedAtAfter(restaurantId, Order.OrderStatus.COMPLETED, todayStart);
        BigDecimal todayAvgOrderValue = todayOrders > 0 ? todayRevenue.divide(BigDecimal.valueOf(todayOrders), java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;

        long todayCustomers = orderRepository.countDistinctCustomersByRestaurantIdAndCreatedAtAfter(restaurantId, todayStart);
        long totalCustomers = orderRepository.countDistinctCustomersByRestaurantId(restaurantId);

        // Recent orders
        List<OwnerDashboardResponse.RecentOrder> recentOrders = new ArrayList<>();
        List<Order> recent = orderRepository.findTop5ByRestaurantIdOrderByCreatedAtDesc(restaurantId);
        for (Order o : recent) {
            OwnerDashboardResponse.RecentOrder ro = new OwnerDashboardResponse.RecentOrder();
            ro.setId(o.getOrderId() != null ? o.getOrderId() : String.valueOf(o.getId()));
            ro.setCustomer(o.getCustomerName());
            ro.setItems(o.getItems() != null ? o.getItems().size() : 0);
            ro.setAmount(o.getTotalAmount());
            ro.setStatus(o.getStatus() != null ? o.getStatus().name() : "UNKNOWN");
            recentOrders.add(ro);
        }

        // Inventory alerts
        List<InventoryItem> lowStock = inventoryItemRepository.findLowStockByRestaurantId(restaurantId);
        List<InventoryItem> outOfStock = inventoryItemRepository.findOutOfStockByRestaurantId(restaurantId);

        List<OwnerDashboardResponse.InventoryAlert> lowStockItems = lowStock.stream()
                .map(i -> {
                    OwnerDashboardResponse.InventoryAlert a = new OwnerDashboardResponse.InventoryAlert();
                    a.setName(i.getName());
                    a.setCurrent(i.getCurrentStock());
                    a.setMinimum(i.getMinimumStock());
                    a.setUnit(i.getUnitOfMeasure());
                    return a;
                }).collect(Collectors.toList());

        List<OwnerDashboardResponse.InventoryAlert> outOfStockItems = outOfStock.stream()
                .map(i -> {
                    OwnerDashboardResponse.InventoryAlert a = new OwnerDashboardResponse.InventoryAlert();
                    a.setName(i.getName());
                    a.setCurrent(i.getCurrentStock());
                    a.setMinimum(i.getMinimumStock());
                    a.setUnit(i.getUnitOfMeasure());
                    return a;
                }).collect(Collectors.toList());

        // Popular items
        List<OwnerDashboardResponse.PopularItem> popularItems = new ArrayList<>();
        List<Object[]> rawPopular = orderItemRepository.findPopularItemsByRestaurantId(restaurantId);
        for (Object[] row : rawPopular) {
            OwnerDashboardResponse.PopularItem pi = new OwnerDashboardResponse.PopularItem();
            pi.setName((String) row[0]);
            pi.setOrders(((Number) row[1]).intValue());
            pi.setRevenue((BigDecimal) row[2]);
            pi.setTrend("stable");
            popularItems.add(pi);
        }

        // Staff not yet tracked in dedicated table; return empty snapshot
        List<OwnerDashboardResponse.StaffPerformance> staffPerformance = Collections.emptyList();

        OwnerDashboardResponse response = new OwnerDashboardResponse();
        response.setTotalRevenue(totalRevenue);
        response.setTotalOrders(totalOrders);
        response.setTotalCustomers(totalCustomers);
        response.setAvgOrderValue(avgOrderValue);
        response.setTodayRevenue(todayRevenue);
        response.setTodayOrders(todayOrders);
        response.setTodayCustomers(todayCustomers);
        response.setTodayAvgOrderValue(todayAvgOrderValue);
        response.setRecentOrders(recentOrders);
        response.setPendingOrders(pendingOrders);
        response.setCompletedOrders(completedOrders);
        response.setLowStockItems(lowStockItems);
        response.setOutOfStockItems(outOfStockItems);
        response.setPopularItems(popularItems);
        response.setActiveStaff(0);
        response.setTotalStaff(0);
        response.setStaffOnBreak(0);
        response.setStaffPerformance(staffPerformance);
        return response;
    }

    public void emitUpdate(Long restaurantId) {
        if (restaurantId == null) return;

        long now = System.currentTimeMillis();
        Long last = lastEmitByRestaurant.get(restaurantId);
        if (last != null && (now - last) < THROTTLE_MILLIS) {
            return;
        }
        lastEmitByRestaurant.put(restaurantId, now);

        try {
            OwnerDashboardResponse snapshot = getDashboard(restaurantId);
            messagingTemplate.convertAndSend(TOPIC_PREFIX + restaurantId + "/owner-dashboard", snapshot);
            log.debug("[OwnerDashboard] Emitted update for restaurant {}", restaurantId);
        } catch (Exception e) {
            log.error("[OwnerDashboard] Failed to emit update for restaurant {}", restaurantId, e);
        }
    }
}
