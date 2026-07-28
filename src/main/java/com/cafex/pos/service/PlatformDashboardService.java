package com.cafex.pos.service;

import com.cafex.pos.dto.CustomerResponse;
import com.cafex.pos.dto.OrderResponse;
import com.cafex.pos.dto.PlatformDashboardResponse;
import com.cafex.pos.dto.RestaurantResponse;
import com.cafex.pos.dto.RestaurantSubscriptionResponse;
import com.cafex.pos.dto.SubscriptionPlansResponse;
import com.cafex.pos.dto.UserResponse;
import com.cafex.pos.event.DashboardRefreshEvent;
import com.cafex.pos.repository.CustomerRepository;
import com.cafex.pos.repository.OrderRepository;
import com.cafex.pos.repository.RestaurantRepository;
import com.cafex.pos.repository.RestaurantSubscriptionRepository;
import com.cafex.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformDashboardService {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantSubscriptionRepository restaurantSubscriptionRepository;
    private final SubscriptionPlansService subscriptionPlansService;
    private final SimpMessagingTemplate messagingTemplate;

    public PlatformDashboardResponse getDashboardSnapshot() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.toLocalDate().atStartOfDay();
        LocalDateTime weekAgo = today.minusDays(7);
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime thirtyDaysAgo = today.minusDays(30);

        PlatformDashboardResponse.RestaurantStats restaurantStats = new PlatformDashboardResponse.RestaurantStats();
        restaurantStats.setTotal(restaurantRepository.count());
        restaurantStats.setNewToday(restaurantRepository.countByCreatedAtAfter(today));
        restaurantStats.setNewThisWeek(restaurantRepository.countByCreatedAtAfter(weekAgo));
        restaurantStats.setActive(restaurantRepository.countActive());
        restaurantStats.setTrial(restaurantSubscriptionRepository.countByStatus("trial"));
        restaurantStats.setExpired(restaurantSubscriptionRepository.countByStatus("expired"));
        restaurantStats.setSuspended(restaurantRepository.countByStatus(com.cafex.pos.entity.Restaurant.RestaurantStatus.SUSPENDED));

        PlatformDashboardResponse.UserStats userMetrics = new PlatformDashboardResponse.UserStats();
        userMetrics.setPlatformOwners(userRepository.countByRole(com.cafex.pos.entity.User.UserRole.platform_owner));
        userMetrics.setRestaurantOwners(userRepository.countByRole(com.cafex.pos.entity.User.UserRole.restaurant_owner));
        userMetrics.setRestaurantManagers(userRepository.countByRole(com.cafex.pos.entity.User.UserRole.restaurant_manager));
        userMetrics.setKitchenManagers(userRepository.countByRole(com.cafex.pos.entity.User.UserRole.kitchen_manager));
        userMetrics.setWaiters(userRepository.countByRole(com.cafex.pos.entity.User.UserRole.waiter));
        userMetrics.setCashiers(userRepository.countByRole(com.cafex.pos.entity.User.UserRole.cashier));
        userMetrics.setEndCustomers(customerRepository.count());
        userMetrics.setNewUsersToday(userRepository.countByCreatedAtAfter(today));
        userMetrics.setNewCustomersToday(customerRepository.countByCreatedAtAfter(today));
        userMetrics.setNewThisWeek(userRepository.countByCreatedAtAfter(weekAgo) + customerRepository.countByCreatedAtAfter(weekAgo));

        long totalOrdersToday = orderRepository.countByCreatedAtAfter(today);
        long completedToday = orderRepository.countByStatusAndCreatedAtAfter(com.cafex.pos.entity.Order.OrderStatus.COMPLETED, today);
        long uncompletedToday = totalOrdersToday - completedToday;
        BigDecimal ordersTodayAmount = orderRepository.sumTotalAmountByStatusAndCreatedAtAfter(com.cafex.pos.entity.Order.OrderStatus.COMPLETED, today);
        long totalCompletedOrders = orderRepository.countByStatus(com.cafex.pos.entity.Order.OrderStatus.COMPLETED);
        BigDecimal totalOrderAmount = orderRepository.sumTotalAmountByStatus(com.cafex.pos.entity.Order.OrderStatus.COMPLETED);

        PlatformDashboardResponse.BusinessPulse businessPulse = new PlatformDashboardResponse.BusinessPulse();
        businessPulse.setOrdersToday(totalOrdersToday);
        businessPulse.setUncompletedOrdersToday(uncompletedToday);
        businessPulse.setCompletedOrdersToday(completedToday);
        businessPulse.setOrdersTodayAmount(ordersTodayAmount != null ? ordersTodayAmount : BigDecimal.ZERO);
        businessPulse.setTotalOrders(totalCompletedOrders);
        businessPulse.setTotalOrderAmount(totalOrderAmount != null ? totalOrderAmount : BigDecimal.ZERO);

        BigDecimal currentMonthRevenue = restaurantSubscriptionRepository.sumFinalAmountByStartDateBetween(monthStart, monthStart.plusMonths(1));
        BigDecimal totalRevenue = restaurantSubscriptionRepository.sumFinalAmount();
        BigDecimal prevMonthRevenue = restaurantSubscriptionRepository.sumFinalAmountByStartDateBetween(monthStart.minusMonths(1), monthStart);

        double revenueGrowth = 0.0;
        if (prevMonthRevenue != null && prevMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            revenueGrowth = currentMonthRevenue.subtract(prevMonthRevenue)
                    .divide(prevMonthRevenue, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (currentMonthRevenue != null && currentMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            revenueGrowth = 100.0;
        }

        List<Object[]> monthlyRows = restaurantSubscriptionRepository.findMonthlyRevenue();
        java.math.BigDecimal[] monthlyRevenue = new java.math.BigDecimal[12];
        for (int i = 0; i < 12; i++) {
            monthlyRevenue[i] = BigDecimal.ZERO;
        }
        for (Object[] row : monthlyRows) {
            Integer monthIndex = (Integer) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            if (monthIndex != null && monthIndex >= 0 && monthIndex < 12 && amount != null) {
                monthlyRevenue[monthIndex] = amount;
            }
        }

        PlatformDashboardResponse.RevenueStats revenueStats = new PlatformDashboardResponse.RevenueStats();
        revenueStats.setCurrentMonth(currentMonthRevenue != null ? currentMonthRevenue : BigDecimal.ZERO);
        revenueStats.setTotal(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        revenueStats.setGrowth(java.math.BigDecimal.valueOf(revenueGrowth)
                .setScale(1, java.math.RoundingMode.HALF_UP).doubleValue());
        revenueStats.setMonthly(java.util.Arrays.asList(monthlyRevenue));

        List<SubscriptionPlansResponse> subscriptionPlans = subscriptionPlansService.getAllSubscriptionPlans();
        List<Object[]> planCountRows = restaurantSubscriptionRepository.countByPlanId();
        Map<Long, Long> planCountsMap = planCountRows.stream()
                .collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).longValue()));
        Map<Long, String> planIdToName = subscriptionPlans.stream()
                .collect(Collectors.toMap(SubscriptionPlansResponse::getId, p -> p.getDisplay_name() != null && !p.getDisplay_name().isEmpty() ? p.getDisplay_name() : p.getName()));

        java.util.List<String> labels = planCountsMap.keySet().stream()
                .map(planIdToName::get)
                .collect(Collectors.toList());
        java.util.List<Long> data = planCountsMap.values().stream().collect(Collectors.toList());

        PlatformDashboardResponse.SubscriptionStats subscriptionStats = new PlatformDashboardResponse.SubscriptionStats();
        subscriptionStats.setLabels(labels);
        subscriptionStats.setData(data);

        long totalTenants = restaurantStats.getTotal();
        long churned = restaurantSubscriptionRepository.countChurnedLast30Days(thirtyDaysAgo);
        double churnRate = totalTenants > 0 ? (double) churned / (double) totalTenants * 100 : 0.0;
        churnRate = java.math.BigDecimal.valueOf(churnRate).setScale(1, java.math.RoundingMode.HALF_UP).doubleValue();

        PlatformDashboardResponse.ChurnStats churnStats = new PlatformDashboardResponse.ChurnStats();
        churnStats.setRate(churnRate);
        churnStats.setTrend(-churnRate);

        return new PlatformDashboardResponse(
                restaurantStats,
                userMetrics,
                businessPulse,
                revenueStats,
                subscriptionStats,
                churnStats
        );
    }

    public void emitDashboardUpdate() {
        try {
            PlatformDashboardResponse metrics = getDashboardSnapshot();
            messagingTemplate.convertAndSend("/topic/platform/dashboard", metrics);
        } catch (Exception e) {
            log.error("Failed to emit dashboard metrics", e);
        }
    }

    @EventListener
    public void onDashboardRefresh(DashboardRefreshEvent event) {
        emitDashboardUpdate();
    }
}
