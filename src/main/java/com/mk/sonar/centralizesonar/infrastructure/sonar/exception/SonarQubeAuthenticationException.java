package com.mk.sonar.centralizesonar.infrastructure.sonar.exception;

/**
 * Exception thrown when authentication fails while communicating with SonarQube.
 * Typically corresponds to HTTP 401 Unauthorized responses.
 */
public class SonarQubeAuthenticationException extends SonarQubeException {

    public SonarQubeAuthenticationException(String message) {
        super(message, 401, "AUTHENTICATION_FAILED");
    }

    /**
     * Constructor with cause for exception chaining.
     */
    public SonarQubeAuthenticationException(String message, Throwable cause) {
        super(message, 401, "AUTHENTICATION_FAILED", cause);
    }
}

