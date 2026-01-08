package com.mk.sonar.centralizesonar.application.sonar.service;

import com.mk.sonar.centralizesonar.domain.sonar.model.Metrics;
import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectCatalog;
import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectKey;
import com.mk.sonar.centralizesonar.domain.sonar.port.SonarQubePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsService.class);
    private final SonarQubePort sonarQubePort;

    public MetricsService(SonarQubePort sonarQubePort) {
        this.sonarQubePort = sonarQubePort;
    }

    public Metrics fetchMetrics(String projectKeyString) {
        logger.debug("Fetching metrics for project: {}", projectKeyString);
        ProjectKey projectKey = new ProjectKey(projectKeyString);
        Metrics metrics = sonarQubePort.getMetrics(projectKey);
        logger.debug("Successfully fetched metrics for project: {}", projectKeyString);
        return metrics;
    }

    public ProjectCatalog fetchProjectCatalog() {
        logger.debug("Fetching project catalog from SonarQube");
        ProjectCatalog projectCatalog = sonarQubePort.getProjectCatalog();
        logger.debug("Successfully fetched project catalog with {} projects", projectCatalog.count());
        return projectCatalog;
    }
}

