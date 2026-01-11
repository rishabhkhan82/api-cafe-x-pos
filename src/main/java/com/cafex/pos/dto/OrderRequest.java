package com.cafex.pos.dto;

import com.cafex.pos.entity.Order;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Slf4j
public class OrderRequest {

    @NotBlank(message = "Order ID is required")
    @Size(min = 3, max = 50, message = "Order ID must be between 3 and 50 characters")
    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("customer_id")
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("table_number")
    private String tableNumber;

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private Order.OrderStatus status = Order.OrderStatus.PENDING;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @Size(max = 500, message = "Special instructions must not exceed 500 characters")
    @JsonProperty("special_instructions")
    private String specialInstructions;

    @JsonProperty("payment_status")
    private Order.PaymentStatus paymentStatus = Order.PaymentStatus.PENDING;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_type")
    private Order.OrderType orderType = Order.OrderType.DINE_IN;

    @JsonProperty("estimated_ready_time")
    private LocalDateTime estimatedReadyTime;

    @JsonProperty("delivered_at")
    private LocalDateTime deliveredAt;

    @JsonProperty("priority")
    private Order.OrderPriority priority = Order.OrderPriority.MEDIUM;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be non-negative")
    @JsonProperty("tax_amount")
    private BigDecimal taxAmount;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    // For updates - optional fields
    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}