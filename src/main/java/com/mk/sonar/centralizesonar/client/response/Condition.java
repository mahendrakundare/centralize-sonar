package com.mk.sonar.centralizesonar.client.response;

public class Condition {
    private final String metricKey;
    private final String status;
    private final String actualValue;
    private final String errorThreshold;

    public Condition(String metricKey, String status, String actualValue, String errorThreshold) {
        this.metricKey = metricKey;
        this.status = status;
        this.actualValue = actualValue;
        this.errorThreshold = errorThreshold;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public String getStatus() {
        return status;
    }

    public String getActualValue() {
        return actualValue;
    }

    public String getErrorThreshold() {
        return errorThreshold;
    }
}
