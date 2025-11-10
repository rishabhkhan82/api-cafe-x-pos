package com.cafex.pos.dto;

import com.cafex.pos.entity.User;
import lombok.Data;

@Data
public class LoginResponse {

    private User user;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}