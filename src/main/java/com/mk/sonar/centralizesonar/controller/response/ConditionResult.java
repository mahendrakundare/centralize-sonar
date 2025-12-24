package com.mk.sonar.centralizesonar.controller.response;

public record ConditionResult(String metricKey, String status, String actualValue, String errorThreshold) {
}
