package com.cafex.pos.dto;

import com.cafex.pos.entity.Customer;
import lombok.Data;

@Data
public class CustomerLoginResponse {

    private Customer customer;
    private String accessToken;
    private long expiresIn;
}