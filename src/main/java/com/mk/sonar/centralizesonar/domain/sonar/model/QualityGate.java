package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.*;
import java.util.stream.Collectors;

public class QualityGate {
    private final String status;
    private final List<Condition> conditions;

    public QualityGate(String status, List<Condition> conditions) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Quality gate status cannot be null or blank");
        }
        this.status = status.trim();
        this.conditions = conditions != null ? new ArrayList<>(conditions) : new ArrayList<>();
    }

    public String status() {
        return status;
    }

    public List<Condition> conditions() {
        return Collections.unmodifiableList(conditions);
    }

    public String evaluate() {
        if (conditions.isEmpty()) {
            return "NONE";
        }

        boolean hasError = conditions.stream().anyMatch(Condition::isViolated);
        if (hasError) {
            return "ERROR";
        }

        boolean allOk = conditions.stream().allMatch(Condition::isMet);
        if (allOk) {
            return "OK";
        }

        return "WARN";
    }

    public boolean isPassed() {
        return "OK".equalsIgnoreCase(status);
    }

    public boolean hasFailed() {
        return "ERROR".equalsIgnoreCase(status);
    }

    public List<Condition> getFailedConditions() {
        return conditions.stream()
                .filter(Condition::isViolated)
                .collect(Collectors.toList());
    }

    public List<Condition> getPassedConditions() {
        return conditions.stream()
                .filter(Condition::isMet)
                .collect(Collectors.toList());
    }

    public boolean hasFailedConditions() {
        return !getFailedConditions().isEmpty();
    }

    public int conditionCount() {
        return conditions.size();
    }

    public Optional<Condition> getCondition(String metricKey) {
        return conditions.stream()
                .filter(c -> c.metricKey().equalsIgnoreCase(metricKey))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualityGate that = (QualityGate) o;
        return Objects.equals(status, that.status) && Objects.equals(conditions, that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, conditions);
    }

    @Override
    public String toString() {
        return String.format("QualityGate{status='%s', conditions=%d}", status, conditions.size());
    }
}

