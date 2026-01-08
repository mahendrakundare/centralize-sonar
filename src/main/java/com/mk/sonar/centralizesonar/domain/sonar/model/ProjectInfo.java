package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.Objects;

public final class ProjectInfo {
    private final ProjectKey projectKey;
    private final String projectName;

    public ProjectInfo(ProjectKey projectKey, String projectName) {
        if (projectKey == null) {
            throw new IllegalArgumentException("Project key cannot be null");
        }
        this.projectKey = projectKey;
        this.projectName = projectName != null ? projectName.trim() : null;
    }

    public ProjectKey projectKey() {
        return projectKey;
    }

    public String projectName() {
        return projectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectInfo that = (ProjectInfo) o;
        return Objects.equals(projectKey, that.projectKey) && Objects.equals(projectName, that.projectName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectKey, projectName);
    }

    @Override
    public String toString() {
        return String.format("ProjectInfo{projectKey='%s', projectName='%s'}", projectKey, projectName);
    }
}

