package com.mk.sonar.centralizesonar.domain.sonar.exception;

public class ProjectNotFoundException extends RuntimeException {
    private final String projectKey;

    public ProjectNotFoundException(String projectKey) {
        super(String.format("Project '%s' not found in SonarQube", projectKey));
        this.projectKey = projectKey;
    }

    public String projectKey() {
        return projectKey;
    }
}

