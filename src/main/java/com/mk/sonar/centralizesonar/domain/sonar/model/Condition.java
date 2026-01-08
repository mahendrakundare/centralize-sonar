package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.Objects;
import java.util.Optional;

public final class Condition {
    private final String metricKey;
    private final String status;
    private final String actualValue;
    private final String errorThreshold;

    public Condition(String metricKey, String status, String actualValue, String errorThreshold) {
        if (metricKey == null || metricKey.isBlank()) {
            throw new IllegalArgumentException("Metric key cannot be null or blank");
        }
        this.metricKey = metricKey.trim();
        this.status = status != null ? status.trim() : null;
        this.actualValue = actualValue != null ? actualValue.trim() : null;
        this.errorThreshold = errorThreshold != null ? errorThreshold.trim() : null;
    }

    public String metricKey() {
        return metricKey;
    }

    public String status() {
        return status;
    }

    public String actualValue() {
        return actualValue;
    }

    public String errorThreshold() {
        return errorThreshold;
    }

    public boolean isMet() {
        return "OK".equalsIgnoreCase(status);
    }

    public boolean isViolated() {
        return "ERROR".equalsIgnoreCase(status);
    }

    public int compareThreshold() {
        if (actualValue == null || errorThreshold == null) {
            throw new IllegalArgumentException("Cannot compare null values");
        }

        try {
            double actual = Double.parseDouble(actualValue);
            double threshold = Double.parseDouble(errorThreshold);
            return Double.compare(actual, threshold);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot compare non-numeric values", e);
        }
    }

    public boolean hasThreshold() {
        return errorThreshold != null && !errorThreshold.isBlank();
    }

    public boolean hasActualValue() {
        return actualValue != null && !actualValue.isBlank();
    }

    public Optional<Double> parseThreshold() {
        if (!hasThreshold()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Double.parseDouble(errorThreshold));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return Objects.equals(metricKey, condition.metricKey) &&
                Objects.equals(status, condition.status) &&
                Objects.equals(actualValue, condition.actualValue) &&
                Objects.equals(errorThreshold, condition.errorThreshold);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricKey, status, actualValue, errorThreshold);
    }

    @Override
    public String toString() {
        return String.format("Condition{metricKey='%s', status='%s', actualValue='%s', errorThreshold='%s'}",
                metricKey, status, actualValue, errorThreshold);
    }
}

