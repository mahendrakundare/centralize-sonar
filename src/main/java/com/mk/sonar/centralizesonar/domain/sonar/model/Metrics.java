package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.*;
import java.util.stream.Collectors;

public class Metrics {
    private final List<Measure> measures;

    public Metrics(List<Measure> measures) {
        this.measures = measures != null ? new ArrayList<>(measures) : new ArrayList<>();
    }

    public List<Measure> measures() {
        return Collections.unmodifiableList(measures);
    }

    public Optional<Measure> getMeasure(String metricName) {
        return measures.stream()
                .filter(m -> m.metric().equalsIgnoreCase(metricName))
                .findFirst();
    }

    public Optional<String> getMetricValue(String metricName) {
        return getMeasure(metricName)
                .map(Measure::value);
    }

    public boolean hasMetric(String metricName) {
        return getMeasure(metricName).isPresent();
    }

    public List<Measure> getNumericMeasures() {
        return measures.stream()
                .filter(Measure::isNumeric)
                .collect(Collectors.toList());
    }

    public Map<String, Double> aggregate() {
        Map<String, Double> aggregated = new HashMap<>();
        for (Measure measure : measures) {
            measure.parseValue().ifPresent(value -> aggregated.put(measure.metric(), value));
        }
        return aggregated;
    }

    public int count() {
        return measures.size();
    }

    public boolean isEmpty() {
        return measures.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metrics metrics = (Metrics) o;
        return Objects.equals(measures, metrics.measures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measures);
    }

    @Override
    public String toString() {
        return String.format("Metrics{measures=%d}", measures.size());
    }
}

