package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantReportResponse;
import com.cafex.pos.entity.InventoryItem;
import com.cafex.pos.entity.Order;
import com.cafex.pos.entity.OrderItem;
import com.cafex.pos.entity.Restaurant;
import com.cafex.pos.repository.InventoryItemRepository;
import com.cafex.pos.repository.OrderItemRepository;
import com.cafex.pos.repository.OrderRepository;
import com.cafex.pos.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RestaurantReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final RestaurantRepository restaurantRepository;

    public RestaurantReportResponse getRestaurantReport(String reportType, String startDate, String endDate, Long restaurantId) {
        log.info("Generating restaurant report - type: {}, startDate: {}, endDate: {}, restaurantId: {}", reportType, startDate, endDate, restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

        RestaurantReportResponse.ReportMeta reportMeta = buildReportMeta(restaurant, reportType, startDate, endDate);

        switch (reportType.toUpperCase()) {
            case "INVENTORY_STATUS":
                return buildInventoryStatusReport(reportMeta, restaurantId);
            case "TOP_SELLING_ITEMS":
                return buildTopSellingItemsReport(reportMeta, restaurantId, startDate, endDate);
            case "CATEGORY_WISE_SALE":
                return buildCategoryWiseSaleReport(reportMeta, restaurantId, startDate, endDate);
            case "TAX_DISCOUNTS":
                return buildTaxDiscountsReport(reportMeta, restaurantId, startDate, endDate);
            case "SALES_SUMMARY":
            default:
                return buildSalesSummaryReport(reportMeta, restaurantId, startDate, endDate);
        }
    }

    private RestaurantReportResponse.ReportMeta buildReportMeta(Restaurant restaurant, String reportType, String startDate, String endDate) {
        String formattedReportType = formatReportTypeLabel(reportType);
        String period = "";
        if (startDate != null && endDate != null) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            try {
                LocalDate sDate = LocalDate.parse(startDate, inputFormatter);
                LocalDate eDate = LocalDate.parse(endDate, inputFormatter);
                String from = sDate.format(outputFormatter);
                String to = eDate.format(outputFormatter);
                period = sDate.equals(eDate) ? from : from + " to " + to;
            } catch (Exception e) {
                period = startDate + " to " + endDate;
            }
        }

        return new RestaurantReportResponse.ReportMeta(
            restaurant.getName(),
            restaurant.getAddress(),
            restaurant.getPhone(),
            restaurant.getEmail(),
            restaurant.getGstNumber(),
            reportType.toUpperCase(),
            formattedReportType,
            startDate != null ? startDate : "",
            endDate != null ? endDate : "",
            LocalDateTime.now().toString(),
            period
        );
    }

    private String formatReportTypeLabel(String reportType) {
        if (reportType == null) return "Sales Summary";
        switch (reportType.toUpperCase()) {
            case "INVENTORY_STATUS": return "Inventory Status";
            case "TOP_SELLING_ITEMS": return "Top Selling Items";
            case "CATEGORY_WISE_SALE": return "Category Wise Sale";
            case "TAX_DISCOUNTS": return "Tax & Discounts";
            case "SALES_SUMMARY":
            default: return "Sales Summary";
        }
    }

    private List<Order> getCompletedOrdersInRange(Long restaurantId, String startDate, String endDate) {
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId));
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), Order.OrderStatus.COMPLETED));
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("createdAt"), startDateTime, endDateTime));
            return predicate;
        };
        return orderRepository.findAll(spec);
    }

    private RestaurantReportResponse buildSalesSummaryReport(RestaurantReportResponse.ReportMeta reportMeta, Long restaurantId, String startDate, String endDate) {
        List<Order> orders = getCompletedOrdersInRange(restaurantId, startDate, endDate);

        int totalOrders = orders.size();
        BigDecimal totalRevenue = orders.stream().map(Order::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        BigDecimal avgOrderValue = totalOrders > 0 ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        int completedOrders = (int) orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED).count();
        int paidOrders = (int) orders.stream().filter(o -> o.getPaymentStatus() == Order.PaymentStatus.PAID).count();

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("total_orders", totalOrders);
        statistics.put("total_revenue", totalRevenue.toPlainString());
        statistics.put("avg_order_value", avgOrderValue.toPlainString());
        statistics.put("completed_orders", completedOrders);
        statistics.put("paid_orders", paidOrders);

        List<Map<String, Object>> data = orders.stream().map(order -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("order_id", order.getOrderId());
            row.put("customer_name", order.getCustomerName());
            row.put("table_number", order.getTableNumber());
            row.put("order_type", order.getOrderType() != null ? order.getOrderType().name() : "");
            row.put("payment_method", order.getPaymentMethod());
            row.put("total_amount", order.getTotalAmount() != null ? order.getTotalAmount().toPlainString() : "0.00");
            row.put("status", order.getStatus() != null ? order.getStatus().name() : "");
            row.put("created_at", order.getCreatedAt() != null ? order.getCreatedAt().toString() : "");
            return row;
        }).collect(Collectors.toList());

        return new RestaurantReportResponse(reportMeta, statistics, data);
    }

    private RestaurantReportResponse buildInventoryStatusReport(RestaurantReportResponse.ReportMeta reportMeta, Long restaurantId) {
        List<InventoryItem> items = inventoryItemRepository.findByRestaurantId(restaurantId);

        BigDecimal totalStockValue = items.stream()
                .map(item -> item.getUnitCost() != null && item.getCurrentStock() != null
                        ? item.getUnitCost().multiply(item.getCurrentStock()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        long lowStockItems = items.stream().filter(InventoryItem::isLowStock).count();
        long outOfStockItems = items.stream().filter(InventoryItem::isOutOfStock).count();

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("total_items", items.size());
        statistics.put("total_stock_value", totalStockValue.toPlainString());
        statistics.put("low_stock_items", lowStockItems);
        statistics.put("out_of_stock_items", outOfStockItems);

        List<Map<String, Object>> data = items.stream().map(item -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("item_id", item.getItemId());
            row.put("name", item.getName());
            row.put("category", item.getCategory());
            row.put("current_stock", item.getCurrentStock() != null ? item.getCurrentStock().toPlainString() : "0");
            row.put("min_stock", item.getMinimumStock() != null ? item.getMinimumStock().toPlainString() : "0");
            row.put("max_stock", item.getMaximumStock() != null ? item.getMaximumStock().toPlainString() : "0");
            row.put("selling_price", item.getSellingPrice() != null ? item.getSellingPrice().toPlainString() : "0.00");
            BigDecimal stockValue = item.getUnitCost() != null && item.getCurrentStock() != null
                    ? item.getUnitCost().multiply(item.getCurrentStock()) : BigDecimal.ZERO;
            row.put("stock_value", stockValue.toPlainString());
            String stockStatus;
            if (item.isOutOfStock()) stockStatus = "Out of Stock";
            else if (item.isLowStock()) stockStatus = "Low Stock";
            else if (item.getMaximumStock() != null && item.getCurrentStock() != null && item.getCurrentStock().compareTo(item.getMaximumStock()) >= 0) stockStatus = "Overstocked";
            else stockStatus = "In Stock";
            row.put("stock_status", stockStatus);
            return row;
        }).collect(Collectors.toList());

        return new RestaurantReportResponse(reportMeta, statistics, data);
    }

    private RestaurantReportResponse buildTopSellingItemsReport(RestaurantReportResponse.ReportMeta reportMeta, Long restaurantId, String startDate, String endDate) {
        List<Order> orders = getCompletedOrdersInRange(restaurantId, startDate, endDate);

        Map<String, BigDecimal[]> itemStats = new LinkedHashMap<>();
        for (Order order : orders) {
            if (order.getItems() == null) continue;
            for (OrderItem item : order.getItems()) {
                String key = item.getMenuItemName() != null ? item.getMenuItemName() : "Unknown";
                itemStats.computeIfAbsent(key, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});
                BigDecimal totalQty = itemStats.get(key)[0];
                BigDecimal totalRevenue = itemStats.get(key)[1];
                BigDecimal orderCount = itemStats.get(key)[2];
                itemStats.put(key, new BigDecimal[]{
                    totalQty.add(BigDecimal.valueOf(item.getQuantity())),
                    totalRevenue.add(item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO),
                    orderCount.add(BigDecimal.ONE)
                });
            }
        }

        BigDecimal totalItemsSold = itemStats.values().stream().map(a -> a[0]).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        BigDecimal totalItemRevenue = itemStats.values().stream().map(a -> a[1]).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        String topItemName = "";
        BigDecimal topItemRevenue = BigDecimal.ZERO;
        if (!itemStats.isEmpty()) {
            Map.Entry<String, BigDecimal[]> top = itemStats.entrySet().stream()
                    .max(Map.Entry.comparingByValue(Comparator.comparing(a -> a[1])))
                    .orElse(null);
            if (top != null) {
                topItemName = top.getKey();
                topItemRevenue = top.getValue()[1];
            }
        }

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("total_items_sold", totalItemsSold.toPlainString());
        statistics.put("total_item_revenue", totalItemRevenue.toPlainString());
        statistics.put("top_item_name", topItemName);
        statistics.put("top_item_revenue", topItemRevenue.toPlainString());

        List<Map<String, Object>> data = itemStats.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue()[1].compareTo(e1.getValue()[1]))
                .map(entry -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("menu_item_name", entry.getKey());
                    row.put("total_quantity", entry.getValue()[0].toPlainString());
                    row.put("total_revenue", entry.getValue()[1].toPlainString());
                    BigDecimal avgPrice = entry.getValue()[0].compareTo(BigDecimal.ZERO) > 0
                            ? entry.getValue()[1].divide(entry.getValue()[0], 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    row.put("avg_unit_price", avgPrice.toPlainString());
                    row.put("order_count", entry.getValue()[2].toPlainString());
                    return row;
                }).collect(Collectors.toList());

        return new RestaurantReportResponse(reportMeta, statistics, data);
    }

    private RestaurantReportResponse buildCategoryWiseSaleReport(RestaurantReportResponse.ReportMeta reportMeta, Long restaurantId, String startDate, String endDate) {
        List<Order> orders = getCompletedOrdersInRange(restaurantId, startDate, endDate);

        Map<String, BigDecimal[]> categoryStats = new LinkedHashMap<>();
        for (Order order : orders) {
            if (order.getItems() == null) continue;
            for (OrderItem item : order.getItems()) {
                String key = item.getCategory() != null && !item.getCategory().isEmpty() ? item.getCategory() : "Uncategorized";
                categoryStats.computeIfAbsent(key, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});
                BigDecimal totalQty = categoryStats.get(key)[0];
                BigDecimal totalRevenue = categoryStats.get(key)[1];
                BigDecimal itemCount = categoryStats.get(key)[2];
                categoryStats.put(key, new BigDecimal[]{
                    totalQty.add(BigDecimal.valueOf(item.getQuantity())),
                    totalRevenue.add(item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO),
                    itemCount.add(BigDecimal.ONE)
                });
            }
        }

        BigDecimal totalCategoryRevenue = categoryStats.values().stream().map(a -> a[1]).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        String topCategoryName = "";
        BigDecimal topCategoryRevenue = BigDecimal.ZERO;
        if (!categoryStats.isEmpty()) {
            Map.Entry<String, BigDecimal[]> top = categoryStats.entrySet().stream()
                    .max(Map.Entry.comparingByValue(Comparator.comparing(a -> a[1])))
                    .orElse(null);
            if (top != null) {
                topCategoryName = top.getKey();
                topCategoryRevenue = top.getValue()[1];
            }
        }

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("total_categories", categoryStats.size());
        statistics.put("total_category_revenue", totalCategoryRevenue.toPlainString());
        statistics.put("top_category_name", topCategoryName);
        statistics.put("top_category_revenue", topCategoryRevenue.toPlainString());

        List<Map<String, Object>> data = categoryStats.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue()[1].compareTo(e1.getValue()[1]))
                .map(entry -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("category", entry.getKey());
                    row.put("total_quantity", entry.getValue()[0].toPlainString());
                    row.put("total_revenue", entry.getValue()[1].toPlainString());
                    row.put("item_count", entry.getValue()[2].toPlainString());
                    BigDecimal avgPrice = entry.getValue()[2].compareTo(BigDecimal.ZERO) > 0
                            ? entry.getValue()[1].divide(entry.getValue()[2], 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    row.put("avg_unit_price", avgPrice.toPlainString());
                    return row;
                }).collect(Collectors.toList());

        return new RestaurantReportResponse(reportMeta, statistics, data);
    }

    private RestaurantReportResponse buildTaxDiscountsReport(RestaurantReportResponse.ReportMeta reportMeta, Long restaurantId, String startDate, String endDate) {
        List<Order> orders = getCompletedOrdersInRange(restaurantId, startDate, endDate);

        BigDecimal totalTaxCollected = orders.stream()
                .map(Order::getTaxAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        BigDecimal totalDiscountGiven = orders.stream()
                .map(Order::getDiscountAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        BigDecimal totalLoyaltyDiscount = orders.stream()
                .map(Order::getLoyaltyDiscountAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        BigDecimal avgTaxPerOrder = orders.size() > 0 ? totalTaxCollected.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgDiscountPerOrder = orders.size() > 0 ? totalDiscountGiven.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("total_tax_collected", totalTaxCollected.toPlainString());
        statistics.put("total_discount_given", totalDiscountGiven.toPlainString());
        statistics.put("total_loyalty_discount", totalLoyaltyDiscount.toPlainString());
        statistics.put("avg_tax_per_order", avgTaxPerOrder.toPlainString());
        statistics.put("avg_discount_per_order", avgDiscountPerOrder.toPlainString());

        List<Map<String, Object>> data = orders.stream().map(order -> {
            BigDecimal tax = order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO;
            BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal loyalty = order.getLoyaltyDiscountAmount() != null ? order.getLoyaltyDiscountAmount() : BigDecimal.ZERO;
            BigDecimal netAmount = order.getTotalAmount() != null ? order.getTotalAmount().subtract(tax).subtract(discount).subtract(loyalty) : BigDecimal.ZERO;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("order_id", order.getOrderId());
            row.put("status", order.getStatus() != null ? order.getStatus().name() : "");
            row.put("order_type", order.getOrderType() != null ? order.getOrderType().name() : "");
            row.put("total_amount", order.getTotalAmount() != null ? order.getTotalAmount().toPlainString() : "0.00");
            row.put("tax_amount", tax.toPlainString());
            row.put("discount_amount", discount.toPlainString());
            row.put("loyalty_discount", loyalty.toPlainString());
            row.put("net_amount", netAmount.toPlainString());
            row.put("created_at", order.getCreatedAt() != null ? order.getCreatedAt().toString() : "");
            return row;
        }).collect(Collectors.toList());

        return new RestaurantReportResponse(reportMeta, statistics, data);
    }
}
