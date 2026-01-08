package com.mk.sonar.centralizesonar.application.sonar.service;

import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectKey;
import com.mk.sonar.centralizesonar.domain.sonar.model.QualityGate;
import com.mk.sonar.centralizesonar.domain.sonar.port.SonarQubePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QualityGateService {

    private static final Logger logger = LoggerFactory.getLogger(QualityGateService.class);
    private final SonarQubePort sonarQubePort;

    public QualityGateService(SonarQubePort sonarQubePort) {
        this.sonarQubePort = sonarQubePort;
    }

    public QualityGate fetchQualityGate(String projectKeyString) {
        logger.debug("Fetching quality gate for project: {}", projectKeyString);
        ProjectKey projectKey = new ProjectKey(projectKeyString);
        QualityGate qualityGate = sonarQubePort.getQualityGateStatus(projectKey);
        logger.debug("Successfully fetched quality gate for project: {}", projectKeyString);
        return qualityGate;
    }
}

