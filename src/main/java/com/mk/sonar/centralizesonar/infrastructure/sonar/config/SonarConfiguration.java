package com.mk.sonar.centralizesonar.infrastructure.sonar.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "sonar")
@Validated
public record SonarConfiguration(
        @NotBlank(message = "SonarQube URL is required")
        String url,
        String token,
        String username,
        String password,
        @NotBlank(message = "Metric keys are required")
        String metricKeys
) {
}

