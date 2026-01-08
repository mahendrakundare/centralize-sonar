package com.mk.sonar.centralizesonar.infrastructure.sonar.client.feign;

import com.mk.sonar.centralizesonar.config.FeignRetryConfiguration;
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

        if (!isRetryable(e)) {
            logger.debug("Non-retryable error encountered. Propagating exception without retry.");
            throw e;
        }

        logger.warn("Retry attempt {}/{} after {}ms. Error: {}",
                attempt, maxAttempts, currentInterval, e.getMessage());

        try {
            long jitter = (long) (currentInterval * 0.1 * random.nextDouble());
            long sleepTime = currentInterval + jitter;
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw e;
        }

        attempt++;
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
        int status = e.status();

        if (status <= 0) {
            return true;
        }

        if (status >= 500 && status < 600) {
            return true;
        }

        if (status == 429) {
            return true;
        }

        return false;
    }
}

