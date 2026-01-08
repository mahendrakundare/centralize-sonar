package com.mk.sonar.centralizesonar.infrastructure.sonar.exception;

public class ProjectNotFoundException extends SonarQubeException {
    private final String projectKey;

    public ProjectNotFoundException(String projectKey) {
        super(String.format("Project '%s' not found in SonarQube", projectKey), 404, "PROJECT_NOT_FOUND");
        this.projectKey = projectKey;
    }

    public String projectKey() {
        return projectKey;
    }
}

