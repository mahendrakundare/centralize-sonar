package com.mk.sonar.centralizesonar.infrastructure.sonar.exception;

/**
 * Exception thrown when an error occurs while communicating with SonarQube service.
 * Used for service-level errors (5xx status codes).
 */
public class SonarQubeServiceException extends SonarQubeException {
    public SonarQubeServiceException(String message, int statusCode) {
        super(message, statusCode, "SONARQUBE_SERVICE_ERROR");
    }

    public SonarQubeServiceException(String message, int statusCode, Throwable cause) {
        super(message, statusCode, "SONARQUBE_SERVICE_ERROR", cause);
    }
}

