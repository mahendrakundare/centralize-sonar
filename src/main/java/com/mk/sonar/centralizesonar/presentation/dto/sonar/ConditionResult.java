package com.mk.sonar.centralizesonar.presentation.dto.sonar;

public record ConditionResult(String metricKey, String status, String actualValue, String errorThreshold) {
}

