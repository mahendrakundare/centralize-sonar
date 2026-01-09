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

import static com.mk.sonar.centralizesonar.presentation.filter.CorrelationIdConstants.MDC_KEY;

@Component
@Order(2)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Skip logging for actuator endpoints to reduce noise
        if (request.getRequestURI().startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String correlationId = MDC.get(MDC_KEY);

        // Log incoming request
        logger.info("Incoming request: method={}, uri={}, queryString={}, correlationId={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString() != null ? request.getQueryString() : "",
                correlationId != null ? correlationId : "N/A");

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Log outgoing response
            logger.info("Outgoing response: status={}, duration={}ms, correlationId={}",
                    response.getStatus(),
                    duration,
                    correlationId != null ? correlationId : "N/A");
        }
    }
}

