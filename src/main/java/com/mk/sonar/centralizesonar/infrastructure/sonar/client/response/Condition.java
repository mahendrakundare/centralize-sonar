package com.mk.sonar.centralizesonar.infrastructure.sonar.client.response;

public record Condition(
        String metricKey,
        String status,
        String actualValue,
        String errorThreshold
) {
}

