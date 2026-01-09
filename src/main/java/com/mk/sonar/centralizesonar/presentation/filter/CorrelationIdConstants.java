package com.mk.sonar.centralizesonar.presentation.filter;

/**
 * Constants for correlation ID handling across the application.
 * Following industry best practices for centralized configuration.
 */
public final class CorrelationIdConstants {

    /**
     * MDC key used to store correlation ID in thread-local context
     */
    public static final String MDC_KEY = "correlationId";

    /**
     * Primary HTTP header name for correlation ID
     * Industry standard: X-Request-ID is the most widely adopted header name
     */
    public static final String HEADER_NAME = "X-Request-ID";

    /**
     * Alternative HTTP header name for correlation ID
     * Some systems use X-Correlation-ID as an alternative
     */
    public static final String ALTERNATIVE_HEADER_NAME = "X-Correlation-ID";

    /**
     * Maximum length for correlation ID to prevent header injection attacks
     */
    public static final int MAX_CORRELATION_ID_LENGTH = 128;

    private CorrelationIdConstants() {
        // Utility class - prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}

