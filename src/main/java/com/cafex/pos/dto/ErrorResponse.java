package com.cafex.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Integer status;
    private Map<String, String[]> fieldErrors;
    private Object details;
    private LocalDateTime timestamp;

    public static ErrorResponse of(String code, String message, Integer status) {
        return new ErrorResponse(code, message, status, null, null, LocalDateTime.now());
    }

    public static ErrorResponse of(String code, String message, Integer status, Map<String, String[]> fieldErrors) {
        return new ErrorResponse(code, message, status, fieldErrors, null, LocalDateTime.now());
    }
}
