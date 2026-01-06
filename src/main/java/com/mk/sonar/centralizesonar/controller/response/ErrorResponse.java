package com.mk.sonar.centralizesonar.controller.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String errorCode,
        int statusCode,
        LocalDateTime timestamp,
        String path
) {
    public ErrorResponse(String message, String errorCode, int statusCode, String path) {
        this(message, errorCode, statusCode, LocalDateTime.now(), path);
    }
}

