package com.mk.sonar.centralizesonar.client;

import com.mk.sonar.centralizesonar.exception.ProjectNotFoundException;
import com.mk.sonar.centralizesonar.exception.SonarQubeAuthenticationException;
import com.mk.sonar.centralizesonar.exception.SonarQubeServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SonarErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String message = extractErrorMessage(response);

        return switch (status) {
            case 401, 403 -> new SonarQubeAuthenticationException(
                message != null ? message : "Authentication failed. Please check your credentials."
            );
            case 404 -> {
                // Try to extract project key from error message
                String projectKey = extractProjectKey(message);
                yield projectKey != null 
                    ? new ProjectNotFoundException(projectKey)
                    : new SonarQubeServiceException("Resource not found", 404);
            }
            case 400 -> new SonarQubeServiceException(
                message != null ? message : "Bad request", 400
            );
            case 500, 502, 503, 504 -> new SonarQubeServiceException(
                message != null ? message : "SonarQube service unavailable", status
            );
            default -> new SonarQubeServiceException(
                message != null ? message : "Unexpected error occurred", status
            );
        };
    }

    private String extractErrorMessage(Response response) {
        try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {
            if (body != null) {
                return new String(body.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            // Ignore and return null
        }
        return null;
    }

    private String extractProjectKey(String errorMessage) {
        if (errorMessage != null && errorMessage.contains("Project")) {
            // Try to extract project key from error message like:
            // {"errors":[{"msg":"Project 'my-project' not found"}]}
            int start = errorMessage.indexOf("'");
            int end = errorMessage.indexOf("'", start + 1);
            if (start > 0 && end > start) {
                return errorMessage.substring(start + 1, end);
            }
        }
        return null;
    }
}

