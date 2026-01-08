package com.mk.sonar.centralizesonar.infrastructure.sonar.exception;

public class SonarQubeAuthenticationException extends SonarQubeException {
    public SonarQubeAuthenticationException(String message) {
        super(message, 401, "AUTHENTICATION_FAILED");
    }
}

