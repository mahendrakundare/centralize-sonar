package com.mk.sonar.centralizesonar.domain.sonar.port;

import com.mk.sonar.centralizesonar.domain.sonar.model.Metrics;
import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectCatalog;
import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectKey;
import com.mk.sonar.centralizesonar.domain.sonar.model.QualityGate;

public interface SonarQubePort {
    QualityGate getQualityGateStatus(ProjectKey projectKey);

    Metrics getMetrics(ProjectKey projectKey);

    ProjectCatalog getProjectCatalog();
}

