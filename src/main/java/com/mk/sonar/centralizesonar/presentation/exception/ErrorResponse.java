package com.mk.sonar.centralizesonar.presentation.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response structure following industry best practices.
 * Provides consistent error format across all API endpoints.
 */
public record ErrorResponse(
        String message,
        String errorCode,
        int statusCode,
        String path,
        String correlationId,
        Instant timestamp,
        Map<String, Object> details
) {

    public ErrorResponse(String message, String errorCode, int statusCode, String path, String correlationId) {
        this(message, errorCode, statusCode, path, correlationId, Instant.now(), null);
    }


    public ErrorResponse(String message, String errorCode, int statusCode, String path, String correlationId, Map<String, Object> details) {
        this(message, errorCode, statusCode, path, correlationId, Instant.now(), details);
    }
}

