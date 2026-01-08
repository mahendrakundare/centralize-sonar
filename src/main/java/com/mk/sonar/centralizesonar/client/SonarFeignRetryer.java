package com.mk.sonar.centralizesonar.client;

import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SonarFeignRetryer implements Retryer {

    private static final Logger logger = LoggerFactory.getLogger(SonarFeignRetryer.class);
    private static final Random random = new Random();

    private final int maxAttempts;
    private final long initialInterval;
    private final long maxInterval;
    private final double multiplier;
    private int attempt;
    private long currentInterval;

    public SonarFeignRetryer(FeignRetryConfiguration config) {
        this.maxAttempts = config.maxAttempts();
        this.initialInterval = config.initialInterval();
        this.maxInterval = config.maxInterval();
        this.multiplier = config.multiplier();
        this.attempt = 1;
        this.currentInterval = initialInterval;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt >= maxAttempts) {
            logger.error("Max retry attempts ({}) reached for request. Propagating exception.", maxAttempts);
            throw e;
        }

        // Only retry on transient errors (network errors, timeouts, 5xx)
        // Don't retry on 4xx client errors
        if (!isRetryable(e)) {
            logger.debug("Non-retryable error encountered. Propagating exception without retry.");
            throw e;
        }

        logger.warn("Retry attempt {}/{} after {}ms. Error: {}", 
                attempt, maxAttempts, currentInterval, e.getMessage());

        try {
            // Add jitter to prevent thundering herd problem
            long jitter = (long) (currentInterval * 0.1 * random.nextDouble());
            long sleepTime = currentInterval + jitter;
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw e;
        }

        attempt++;
        // Exponential backoff: multiply interval by multiplier, but cap at maxInterval
        currentInterval = Math.min((long) (currentInterval * multiplier), maxInterval);
    }

    @Override
    public Retryer clone() {
        FeignRetryConfiguration config = new FeignRetryConfiguration();
        config.setMaxAttempts(maxAttempts);
        config.setInitialInterval(initialInterval);
        config.setMaxInterval(maxInterval);
        config.setMultiplier(multiplier);
        return new SonarFeignRetryer(config);
    }

    private boolean isRetryable(RetryableException e) {
        // Retry on network errors, timeouts, and 5xx server errors
        // Don't retry on 4xx client errors (400, 401, 403, 404, etc.)
        int status = e.status();
        
        // If status is 0 or negative, it's likely a network/connection error - retry
        if (status <= 0) {
            return true;
        }
        
        // Retry on 5xx server errors
        if (status >= 500 && status < 600) {
            return true;
        }
        
        // Retry on 429 (Too Many Requests) - transient rate limiting
        if (status == 429) {
            return true;
        }
        
        // Don't retry on 4xx client errors (except 429)
        return false;
    }
}

