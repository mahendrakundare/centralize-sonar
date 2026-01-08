package com.mk.sonar.centralizesonar.infrastructure.sonar.client.response;

public record Measure(
        String metric,
        String value
) {
}

