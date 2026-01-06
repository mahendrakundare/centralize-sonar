package com.mk.sonar.centralizesonar.exception;

public class SonarQubeAuthenticationException extends SonarQubeException {
    public SonarQubeAuthenticationException(String message) {
        super(message, 401, "AUTHENTICATION_FAILED");
    }
}

