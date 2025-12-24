package com.mk.sonar.centralizesonar.client.response;

public class QualityGateClientResponse {
    private final ProjectStatus projectStatus;

    public QualityGateClientResponse(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }
    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }
}
