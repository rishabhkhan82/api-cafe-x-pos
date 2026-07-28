package com.cafex.pos.dto;

import lombok.Data;

@Data
public class PaymentOrderRequest {
    private Long planId;
    private Integer months;
    private Double calculatedAmount;
    private Long restaurantId;
    private String gstPercentage;
    private Double gstAmount;
}