package com.mk.sonar.centralizesonar.exception;

import com.mk.sonar.centralizesonar.controller.response.ErrorResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(
            ProjectNotFoundException ex, WebRequest request) {
        logger.warn("Project not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.errorCode(),
                ex.statusCode(),
                request.getDescription(false).replace("uri=", "")
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
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(SonarQubeServiceException.class)
    public ResponseEntity<ErrorResponse> handleSonarQubeServiceException(
            SonarQubeServiceException ex, WebRequest request) {
        logger.error("SonarQube service error: {} (Status: {})", ex.getMessage(), ex.statusCode());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                ex.errorCode(),
                ex.statusCode(),
                request.getDescription(false).replace("uri=", "")
        );
        HttpStatus httpStatus = HttpStatus.resolve(ex.statusCode());
        return ResponseEntity.status(httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(
            FeignException ex, WebRequest request) {
        logger.error("Feign client error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
                "Error communicating with SonarQube service: " + ex.getMessage(),
                "FEIGN_CLIENT_ERROR",
                ex.status(),
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(ex.status()).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "INVALID_ARGUMENT",
                400,
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred", ex);
        ErrorResponse error = new ErrorResponse(
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                500,
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

