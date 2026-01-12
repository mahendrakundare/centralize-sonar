package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.Objects;

public final class ProjectJiraStatus {
    private final ProjectKey projectKey;
    private final String leadTime;
    private final String cycleTime;

    public ProjectJiraStatus(ProjectKey projectKey, String leadTime, String cycleTime) {
        if (projectKey == null) {
            throw new IllegalArgumentException("Project key cannot be null");
        }
        this.projectKey = projectKey;
        this.leadTime = leadTime != null ? leadTime.trim() : null;
        this.cycleTime = cycleTime != null ? cycleTime.trim() : null;
    }

    public ProjectKey projectKey() {
        return projectKey;
    }

    public String leadTime() {
        return leadTime;
    }

    public String cycleTime() {
        return cycleTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectJiraStatus that = (ProjectJiraStatus) o;
        return Objects.equals(projectKey, that.projectKey) && Objects.equals(leadTime, that.leadTime) && Objects.equals(cycleTime, that.cycleTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectKey, leadTime, cycleTime);
    }

    @Override
    public String toString() {
        return "ProjectJiraStatus{" +
                "projectKey=" + projectKey +
                ", leadTime='" + leadTime + '\'' +
                ", cycleTime='" + cycleTime + '\'' +
                '}';
    }
}

