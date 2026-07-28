package com.cafex.pos.dto;

import lombok.Data;

@Data
public class PaymentOrderResponse {
    private String orderId;
    private String keyId;
    private Double amount;
}