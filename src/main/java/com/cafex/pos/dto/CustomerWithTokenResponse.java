package com.cafex.pos.dto;

import lombok.Data;

@Data
public class CustomerWithTokenResponse {

    private CustomerResponse customer;
    private String accessToken;
    private long expiresIn;
}