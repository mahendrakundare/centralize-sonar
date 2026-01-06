package com.mk.sonar.centralizesonar.client.response;

public record Condition(
        String metricKey,
        String status,
        String actualValue,
        String errorThreshold
) {
}
