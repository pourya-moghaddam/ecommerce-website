package com.ecommerce.common.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private String path;
    private String code;
    private String message;

    private String error;
    private int status;

    public ErrorResponse(String message, String code, String path) {
        this.message = message;
        this.code = code;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, String error, int status, String path) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}