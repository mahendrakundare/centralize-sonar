package com.mk.sonar.centralizesonar.exception;

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

    // Record-style accessors
    public int statusCode() {
        return statusCode;
    }

    public String errorCode() {
        return errorCode;
    }
}

