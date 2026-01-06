package com.mk.sonar.centralizesonar.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sonar")
public record SonarConfiguration(
        String url,
        String token,
        String username,
        String password,
        String metricKeys
) {
}
