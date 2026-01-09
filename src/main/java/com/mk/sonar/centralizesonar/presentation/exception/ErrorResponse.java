package com.mk.sonar.centralizesonar.presentation.exception;

public record ErrorResponse(
        String message,
        String errorCode,
        int statusCode,
        String path,
        String correlationId
) {
}

