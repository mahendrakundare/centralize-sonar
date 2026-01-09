package com.mk.sonar.centralizesonar.presentation.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.mk.sonar.centralizesonar.presentation.filter.CorrelationIdConstants.*;

@Component
@Order(1)
public class RequestCorrelationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestCorrelationFilter.class);

    /**
     * Pattern to validate correlation ID format
     * Allows alphanumeric characters, hyphens, and underscores
     * Prevents header injection attacks
     */
    private static final Pattern VALID_CORRELATION_ID_PATTERN =
            Pattern.compile("^[a-zA-Z0-9\\-_]+$");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String correlationId = extractOrGenerateCorrelationId(request);

            MDC.put(MDC_KEY, correlationId);

            response.setHeader(HEADER_NAME, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String extractOrGenerateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(HEADER_NAME);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = request.getHeader(ALTERNATIVE_HEADER_NAME);
        }

        if (correlationId != null && !correlationId.isBlank()) {
            if (correlationId.length() > MAX_CORRELATION_ID_LENGTH) {
                logger.warn("Correlation ID too long (length={}), generating new one",
                        correlationId.length());
                correlationId = null;
            } else if (!VALID_CORRELATION_ID_PATTERN.matcher(correlationId).matches()) {
                logger.warn("Invalid correlation ID format, generating new one: {}", correlationId);
                correlationId = null;
            }
        }

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        return correlationId;
    }
}

