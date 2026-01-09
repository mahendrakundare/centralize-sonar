package com.mk.sonar.centralizesonar.presentation.exception;

import com.mk.sonar.centralizesonar.config.FeignRetryConfiguration;
import com.mk.sonar.centralizesonar.domain.sonar.exception.ProjectNotFoundException;
import com.mk.sonar.centralizesonar.infrastructure.sonar.exception.SonarQubeAuthenticationException;
import com.mk.sonar.centralizesonar.infrastructure.sonar.exception.SonarQubeServiceException;
import com.mk.sonar.centralizesonar.infrastructure.sonar.exception.SonarQubeTimeoutException;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mk.sonar.centralizesonar.presentation.filter.CorrelationIdConstants.MDC_KEY;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final FeignRetryConfiguration retryConfiguration;

    public GlobalExceptionHandler(FeignRetryConfiguration retryConfiguration) {
        this.retryConfiguration = retryConfiguration;
    }

    private String getCorrelationId() {
        return MDC.get(MDC_KEY);
    }

    /**
     * Extracts the request path from WebRequest, removing the "uri=" prefix.
     */
    private String extractRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * Checks if the exception or its cause chain contains a timeout exception.
     * Uses SocketTimeoutException for read timeouts and message-based detection for connection timeouts.
     */
    private SonarQubeTimeoutException.TimeoutType detectTimeoutType(Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null) {
            // Check for read timeout (SocketTimeoutException)
            if (cause instanceof SocketTimeoutException) {
                return SonarQubeTimeoutException.TimeoutType.READ_TIMEOUT;
            }

            // Check message for connection timeout indicators
            String message = cause.getMessage();
            if (message != null) {
                String lowerMessage = message.toLowerCase();
                if (lowerMessage.contains("connect timeout") ||
                        lowerMessage.contains("connection timed out") ||
                        lowerMessage.contains("connecttimeout")) {
                    return SonarQubeTimeoutException.TimeoutType.CONNECTION_TIMEOUT;
                }
                if (lowerMessage.contains("read timeout") ||
                        lowerMessage.contains("read timed out") ||
                        lowerMessage.contains("readtimeout")) {
                    return SonarQubeTimeoutException.TimeoutType.READ_TIMEOUT;
                }
            }

            // Check class name for connection timeout (Apache HttpClient)
            String className = cause.getClass().getName();
            if (className.contains("ConnectTimeout") || className.contains("ConnectTimeoutException")) {
                return SonarQubeTimeoutException.TimeoutType.CONNECTION_TIMEOUT;
            }

            cause = cause.getCause();
        }
        return SonarQubeTimeoutException.TimeoutType.UNKNOWN_TIMEOUT;
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(
            ProjectNotFoundException ex, WebRequest request) {
        logger.warn("Project not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "PROJECT_NOT_FOUND",
                404,
                extractRequestPath(request),
                getCorrelationId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SonarQubeAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            SonarQubeAuthenticationException ex, WebRequest request) {
        logger.error("Authentication failed: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.errorCode(),
                ex.statusCode(),
                extractRequestPath(request),
                getCorrelationId()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(SonarQubeServiceException.class)
    public ResponseEntity<ErrorResponse> handleSonarQubeServiceException(
            SonarQubeServiceException ex, WebRequest request) {
        logger.error("SonarQube service error: {} (Status: {})", ex.getMessage(), ex.statusCode());

        Map<String, Object> details = Map.of(
                "sonarStatusCode", ex.statusCode(),
                "sonarErrorCode", ex.errorCode()
        );

        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.errorCode(),
                ex.statusCode(),
                extractRequestPath(request),
                getCorrelationId(),
                details
        );
        HttpStatus httpStatus = HttpStatus.resolve(ex.statusCode());
        return ResponseEntity.status(httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(
            FeignException ex, WebRequest request) {
        logger.error("Feign client error: {}", ex.getMessage(), ex);

        // Check if this is a timeout exception (check cause chain)
        SonarQubeTimeoutException.TimeoutType timeoutType = detectTimeoutType(ex);
        if (timeoutType != SonarQubeTimeoutException.TimeoutType.UNKNOWN_TIMEOUT) {
            return handleTimeoutException(
                    new SonarQubeTimeoutException(
                            buildTimeoutMessage(timeoutType),
                            timeoutType,
                            ex
                    ),
                    request
            );
        }

        int statusCode = ex.status();
        HttpStatus httpStatus;

        // Handle cases where status < 0 (network errors, timeouts, etc.)
        if (statusCode < 0) {
            // Check message for timeout indicators
            String message = ex.getMessage();
            if (message != null && (message.contains("timeout") || message.contains("Timeout"))) {
                SonarQubeTimeoutException.TimeoutType detectedType =
                        message.contains("connect") ? SonarQubeTimeoutException.TimeoutType.CONNECTION_TIMEOUT
                                : SonarQubeTimeoutException.TimeoutType.READ_TIMEOUT;
                return handleTimeoutException(
                        new SonarQubeTimeoutException(
                                buildTimeoutMessage(detectedType),
                                detectedType,
                                ex
                        ),
                        request
                );
            }
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            statusCode = 503;
        } else if (statusCode >= 600) {
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            statusCode = 503;
        } else {
            httpStatus = HttpStatus.resolve(statusCode);
            if (httpStatus == null) {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                statusCode = 500;
            }
        }

        Map<String, Object> details = new HashMap<>();
        details.put("feignStatus", ex.status());
        if (ex.contentUTF8() != null && !ex.contentUTF8().isEmpty()) {
            details.put("responseBody", ex.contentUTF8());
        }

        ErrorResponse error = new ErrorResponse(
                String.format("Error communicating with SonarQube service: %s", ex.getMessage()),
                "FEIGN_CLIENT_ERROR",
                statusCode,
                extractRequestPath(request),
                getCorrelationId(),
                details
        );
        return ResponseEntity.status(httpStatus).body(error);
    }

    @ExceptionHandler(SonarQubeTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(
            SonarQubeTimeoutException ex, WebRequest request) {
        logger.error("Timeout error communicating with SonarQube: {} (Type: {})",
                ex.getMessage(), ex.timeoutType());

        Map<String, Object> details = new HashMap<>();
        details.put("timeoutType", ex.timeoutType().name());
        details.put("maxRetryAttempts", retryConfiguration.maxAttempts());
        details.put("retryAttempted", true);

        String timeoutMessage = buildTimeoutMessage(ex.timeoutType());
        if (ex.getCause() != null) {
            details.put("originalError", ex.getCause().getClass().getSimpleName());
        }

        ErrorResponse error = new ErrorResponse(
                timeoutMessage,
                ex.errorCode(),
                504,
                extractRequestPath(request),
                getCorrelationId(),
                details
        );
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(error);
    }

    private String buildTimeoutMessage(SonarQubeTimeoutException.TimeoutType timeoutType) {
        String baseMessage = switch (timeoutType) {
            case CONNECTION_TIMEOUT -> "Connection timeout while communicating with SonarQube service";
            case READ_TIMEOUT -> "Read timeout while communicating with SonarQube service";
            case UNKNOWN_TIMEOUT -> "Timeout occurred while communicating with SonarQube service";
        };

        return String.format("%s. Request was retried up to %d times before failing.",
                baseMessage, retryConfiguration.maxAttempts());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "INVALID_ARGUMENT",
                400,
                extractRequestPath(request),
                getCorrelationId()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());

        // Build details map with field-specific validation errors
        Map<String, Object> details = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> Map.of(
                                "message", violation.getMessage(),
                                "invalidValue", violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : "null"
                        ),
                        (existing, replacement) -> existing // Keep first if duplicate keys
                ));

        ErrorResponse error = new ErrorResponse(
                "Validation failed",
                "VALIDATION_ERROR",
                400,
                extractRequestPath(request),
                getCorrelationId(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Method argument validation error: {}", ex.getMessage());

        // Build details map with field-specific validation errors
        Map<String, Object> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> {
                            Map<String, Object> fieldError = new HashMap<>();
                            fieldError.put("message", error.getDefaultMessage());
                            fieldError.put("rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null");
                            if (error.getCode() != null) {
                                fieldError.put("code", error.getCode());
                            }
                            return fieldError;
                        },
                        (existing, replacement) -> existing // Keep first if duplicate keys
                ));

        ErrorResponse error = new ErrorResponse(
                "Validation failed",
                "VALIDATION_ERROR",
                400,
                extractRequestPath(request),
                getCorrelationId(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            org.springframework.web.bind.MissingServletRequestParameterException ex, WebRequest request) {
        logger.warn("Missing request parameter: {}", ex.getMessage());

        Map<String, Object> details = Map.of(
                "parameterName", ex.getParameterName(),
                "parameterType", ex.getParameterType()
        );

        ErrorResponse error = new ErrorResponse(
                String.format("Missing required parameter: %s", ex.getParameterName()),
                "MISSING_PARAMETER",
                400,
                extractRequestPath(request),
                getCorrelationId(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                500,
                extractRequestPath(request),
                getCorrelationId()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

