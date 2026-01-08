package com.mk.sonar.centralizesonar.domain.sonar.exception;

public class InvalidProjectKeyException extends RuntimeException {
    private final String projectKey;

    public InvalidProjectKeyException(String projectKey, String message) {
        super(String.format("Invalid project key '%s': %s", projectKey, message));
        this.projectKey = projectKey;
    }

    public InvalidProjectKeyException(String projectKey, Throwable cause) {
        super(String.format("Invalid project key '%s'", projectKey), cause);
        this.projectKey = projectKey;
    }

    public String projectKey() {
        return projectKey;
    }
}

