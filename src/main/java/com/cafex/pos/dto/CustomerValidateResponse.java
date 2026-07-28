package com.cafex.pos.dto;

import lombok.Data;

@Data
public class CustomerValidateResponse {

    private CustomerResponse customer;
    private String accessToken;
    private long expiresIn;
}