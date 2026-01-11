package com.cafex.pos.dto;

import com.cafex.pos.entity.Order;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Order.OrderPriority priority;

    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    // Helper method to check if order is active
    public boolean isActive() {
        return status != null && !status.equals(Order.OrderStatus.CANCELLED) && !status.equals(Order.OrderStatus.COMPLETED);
    }
}