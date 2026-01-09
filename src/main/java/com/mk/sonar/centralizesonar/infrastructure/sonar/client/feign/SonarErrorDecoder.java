package com.mk.sonar.centralizesonar.infrastructure.sonar.client.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.sonar.centralizesonar.infrastructure.sonar.exception.ProjectNotFoundException;
import com.mk.sonar.centralizesonar.infrastructure.sonar.exception.SonarQubeAuthenticationException;
import com.mk.sonar.centralizesonar.infrastructure.sonar.exception.SonarQubeServiceException;
import com.mk.sonar.centralizesonar.infrastructure.sonar.exception.SonarQubeTimeoutException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SonarErrorDecoder implements ErrorDecoder {

    private static final Logger logger = LoggerFactory.getLogger(SonarErrorDecoder.class);
    private static final String DEFAULT_AUTH_ERROR = "Authentication failed. Please check your credentials.";
    private static final String DEFAULT_NOT_FOUND = "Resource not found";
    private static final String DEFAULT_BAD_REQUEST = "Bad request";
    private static final String DEFAULT_SERVICE_UNAVAILABLE = "SonarQube service unavailable";
    private static final String DEFAULT_UNEXPECTED_ERROR = "Unexpected error occurred";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Returns the message if it's not null or empty, otherwise returns the default message.
     */
    private String getMessageOrDefault(String message, String defaultMessage) {
        return (message != null && !message.isEmpty()) ? message : defaultMessage;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String message = extractErrorMessage(response);

        return switch (status) {
            case 401, 403 -> {
                logger.warn("Authentication error ({}): {}", status, message);
                yield new SonarQubeAuthenticationException(
                        getMessageOrDefault(message, DEFAULT_AUTH_ERROR)
                );
            }
            case 404 -> {
                String projectKey = extractProjectKey(message);
                logger.warn("Resource not found (404). Project key extracted: {}", projectKey);
                yield projectKey != null
                        ? new ProjectNotFoundException(projectKey)
                        : new SonarQubeServiceException(
                        getMessageOrDefault(message, DEFAULT_NOT_FOUND),
                        404
                );
            }
            case 400 -> {
                logger.warn("Bad request (400): {}", message);
                yield new SonarQubeServiceException(
                        getMessageOrDefault(message, DEFAULT_BAD_REQUEST),
                        400
                );
            }
            case 504 -> {
                logger.error("Gateway timeout (504) from SonarQube: {}", message);
                yield new SonarQubeTimeoutException(
                        getMessageOrDefault(message, "Gateway timeout while communicating with SonarQube"),
                        SonarQubeTimeoutException.TimeoutType.READ_TIMEOUT
                );
            }
            case 500, 502, 503 -> {
                logger.error("SonarQube service error ({}): {}", status, message);
                yield new SonarQubeServiceException(
                        getMessageOrDefault(message, DEFAULT_SERVICE_UNAVAILABLE),
                        status
                );
            }
            default -> {
                logger.error("Unexpected error ({}): {}", status, message);
                yield new SonarQubeServiceException(
                        getMessageOrDefault(message, DEFAULT_UNEXPECTED_ERROR),
                        status
                );
            }
        };
    }

    private String extractErrorMessage(Response response) {
        try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {
            if (body != null) {
                return new String(body.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            logger.warn("Failed to extract error message from response", e);
        }
        return null;
    }

    private String extractProjectKey(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            return null;
        }

        try {
            JsonNode root = objectMapper.readTree(errorMessage);
            JsonNode errors = root.get("errors");
            if (errors != null && errors.isArray() && !errors.isEmpty()) {
                JsonNode firstError = errors.get(0);
                JsonNode msgNode = firstError.get("msg");
                if (msgNode != null) {
                    String msg = msgNode.asText();
                    return extractProjectKeyFromMessage(msg);
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to parse error message as JSON, trying simple string extraction", e);
        }

        return extractProjectKeyFromMessage(errorMessage);
    }

    private String extractProjectKeyFromMessage(String message) {
        if (message != null && message.contains("Project")) {
            int start = message.indexOf("'");
            int end = message.indexOf("'", start + 1);
            if (start > 0 && end > start) {
                return message.substring(start + 1, end);
            }
        }
        return null;
    }
}

