package com.mk.sonar.centralizesonar.infrastructure.sonar.health;

import com.mk.sonar.centralizesonar.infrastructure.sonar.client.feign.SonarFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class SonarQubeHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(SonarQubeHealthIndicator.class);
    private final SonarFeignClient sonarFeignClient;

    public SonarQubeHealthIndicator(SonarFeignClient sonarFeignClient) {
        this.sonarFeignClient = sonarFeignClient;
    }

    @Override
    public Health health() {
        try {
            logger.debug("Checking SonarQube health");
            sonarFeignClient.getProjectCatalog();
            logger.debug("SonarQube health check passed");
            return Health.up()
                    .withDetail("status", "SonarQube is reachable and responding")
                    .build();
        } catch (Exception e) {
            logger.warn("SonarQube health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "SonarQube is unreachable or not responding")
                    .build();
        }
    }
}

