package com.mk.sonar.centralizesonar.client.response;


public class Measure {
    private final String metric;
    private final String value;

    public Measure(String metric, String value) {
        this.metric = metric;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public String getValue() {
        return value;
    }
}
