package com.cafex.pos.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String code;
    private final int status;

    public ApiException(String message) {
        this("API_ERROR", message, 400);
    }

    public ApiException(String code, String message) {
        this(code, message, 400);
    }

    public ApiException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
