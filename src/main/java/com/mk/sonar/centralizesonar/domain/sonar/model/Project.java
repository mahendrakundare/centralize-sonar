package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.Objects;

public class Project {
    private final ProjectKey projectKey;
    private QualityGate qualityGate;
    private Metrics metrics;

    public Project(ProjectKey projectKey) {
        if (projectKey == null) {
            throw new IllegalArgumentException("Project key cannot be null");
        }
        this.projectKey = projectKey;
        validate();
    }

    public ProjectKey projectKey() {
        return projectKey;
    }

    public QualityGate qualityGate() {
        return qualityGate;
    }

    public Metrics metrics() {
        return metrics;
    }

    public void setQualityGate(QualityGate qualityGate) {
        if (qualityGate == null) {
            throw new IllegalArgumentException("Quality gate cannot be null");
        }
        this.qualityGate = qualityGate;
    }

    public void setMetrics(Metrics metrics) {
        if (metrics == null) {
            throw new IllegalArgumentException("Metrics cannot be null");
        }
        this.metrics = metrics;
    }

    public void validate() {
        if (projectKey == null || !projectKey.isValid()) {
            throw new IllegalStateException("Project key must be valid");
        }
    }

    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public boolean hasQualityGate() {
        return qualityGate != null;
    }

    public boolean hasMetrics() {
        return metrics != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(projectKey, project.projectKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectKey);
    }

    @Override
    public String toString() {
        return String.format("Project{projectKey='%s'}", projectKey);
    }
}

