package com.mk.sonar.centralizesonar.client.response;

import java.util.List;

public class ProjectStatus {
    private String status;
    private List<Condition> conditions;

    public ProjectStatus(String status, List<Condition> conditions) {
        this.status = status;
        this.conditions = conditions;
    }

    public String getStatus() {
        return status;
    }

    public List<Condition> getConditions() {
        return conditions;
    }
}
