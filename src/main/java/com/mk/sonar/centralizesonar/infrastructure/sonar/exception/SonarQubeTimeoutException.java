package com.mk.sonar.centralizesonar.infrastructure.sonar.exception;

/**
 * Exception thrown when a timeout occurs while communicating with SonarQube.
 * This can be either a connection timeout or a read timeout.
 */
public class SonarQubeTimeoutException extends SonarQubeException {

    private final TimeoutType timeoutType;

    public SonarQubeTimeoutException(String message, TimeoutType timeoutType) {
        super(message, 504, "SONARQUBE_TIMEOUT");
        this.timeoutType = timeoutType;
    }

    public SonarQubeTimeoutException(String message, TimeoutType timeoutType, Throwable cause) {
        super(message, 504, "SONARQUBE_TIMEOUT", cause);
        this.timeoutType = timeoutType;
    }

    public TimeoutType timeoutType() {
        return timeoutType;
    }

    public enum TimeoutType {
        CONNECTION_TIMEOUT,
        READ_TIMEOUT,
        UNKNOWN_TIMEOUT
    }
}

