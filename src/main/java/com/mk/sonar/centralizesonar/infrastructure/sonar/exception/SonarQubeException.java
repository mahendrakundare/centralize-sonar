package com.mk.sonar.centralizesonar.infrastructure.sonar.exception;

/**
 * Base exception class for all SonarQube-related exceptions.
 * Provides common structure with status code and error code.
 */
public class SonarQubeException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;

    public SonarQubeException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public SonarQubeException(String message, int statusCode, String errorCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int statusCode() {
        return statusCode;
    }

    public String errorCode() {
        return errorCode;
    }
}

