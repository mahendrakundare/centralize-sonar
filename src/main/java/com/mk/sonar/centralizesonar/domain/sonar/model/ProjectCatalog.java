package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ProjectCatalog {
    private final List<ProjectInfo> projects;

    public ProjectCatalog(List<ProjectInfo> projects) {
        this.projects = projects != null ? List.copyOf(projects) : Collections.emptyList();
    }

    public List<ProjectInfo> projects() {
        return projects;
    }

    public int count() {
        return projects.size();
    }

    public boolean isEmpty() {
        return projects.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectCatalog that = (ProjectCatalog) o;
        return Objects.equals(projects, that.projects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projects);
    }

    @Override
    public String toString() {
        return String.format("ProjectCatalog{projects=%d}", projects.size());
    }
}

