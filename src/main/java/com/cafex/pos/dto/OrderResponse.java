package com.cafex.pos.dto;

import com.cafex.pos.entity.Order;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("table_number")
    private String tableNumber;

    @JsonProperty("status")
    private Order.OrderStatus status;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("special_instructions")
    private String specialInstructions;

    @JsonProperty("invoice_id")
    private String invoiceId;

    @JsonProperty("payment_status")
    private Order.PaymentStatus paymentStatus;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_type")
    private Order.OrderType orderType;

    @JsonProperty("estimated_ready_time")
    private LocalDateTime estimatedReadyTime;

    @JsonProperty("delivered_at")
    private LocalDateTime deliveredAt;

    @JsonProperty("priority")
    private Order.Priority priority;

    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;

    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;

    @JsonProperty("loyalty_discount_amount")
    private BigDecimal loyaltyDiscountAmount;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("items")
    private List<OrderItemResponse> orderItems;

    // Helper method to check if order is active
    public boolean isActive() {
        return status != null && !status.equals(Order.OrderStatus.CANCELLED) && !status.equals(Order.OrderStatus.COMPLETED);
    }
}