package com.mk.sonar.centralizesonar.exception;

public class ProjectNotFoundException extends SonarQubeException {
    public ProjectNotFoundException(String projectKey) {
        super(
                String.format("Project '%s' not found in SonarQube", projectKey),
                404,
                "PROJECT_NOT_FOUND"
        );
    }
}

