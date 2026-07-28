package com.cafex.pos.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerCreateRequest {
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private Long restaurantId;
}