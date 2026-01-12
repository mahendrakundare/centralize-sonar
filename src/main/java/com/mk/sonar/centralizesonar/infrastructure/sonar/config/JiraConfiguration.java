package com.mk.sonar.centralizesonar.infrastructure.sonar.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jira")
@Validated
public record JiraConfiguration(
        @NotBlank(message = "Jira URL is required")
        String url,
        String token
) {
}

