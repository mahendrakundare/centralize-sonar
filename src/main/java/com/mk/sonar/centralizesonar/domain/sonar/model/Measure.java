package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.Objects;
import java.util.Optional;

public final class Measure {
    private final String metric;
    private final String value;

    public Measure(String metric, String value) {
        if (metric == null || metric.isBlank()) {
            throw new IllegalArgumentException("Metric name cannot be null or blank");
        }
        this.metric = metric.trim();
        this.value = value != null ? value.trim() : null;
    }

    public String metric() {
        return metric;
    }

    public String value() {
        return value;
    }

    public boolean isNumeric() {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Optional<Double> parseValue() {
        if (!isNumeric()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public int compare(double otherValue) {
        return parseValue()
                .map(v -> Double.compare(v, otherValue))
                .orElseThrow(() -> new IllegalArgumentException("Cannot compare non-numeric measure value"));
    }

    public boolean hasValue() {
        return value != null && !value.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measure measure = (Measure) o;
        return Objects.equals(metric, measure.metric) && Objects.equals(value, measure.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metric, value);
    }

    @Override
    public String toString() {
        return String.format("Measure{metric='%s', value='%s'}", metric, value);
    }
}

