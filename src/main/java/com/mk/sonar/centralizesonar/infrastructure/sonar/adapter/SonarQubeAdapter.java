package com.mk.sonar.centralizesonar.infrastructure.sonar.adapter;

import com.mk.sonar.centralizesonar.domain.sonar.model.Metrics;
import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectCatalog;
import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectInfo;
import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectKey;
import com.mk.sonar.centralizesonar.domain.sonar.model.QualityGate;
import com.mk.sonar.centralizesonar.domain.sonar.port.SonarQubePort;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.feign.SonarFeignClient;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.Condition;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.Measure;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.ProjectCatalogClientResponse;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.ProjectComponent;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.QualityGateClientResponse;
import com.mk.sonar.centralizesonar.infrastructure.sonar.config.SonarConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SonarQubeAdapter implements SonarQubePort {

    private static final Logger logger = LoggerFactory.getLogger(SonarQubeAdapter.class);
    private final SonarFeignClient sonarFeignClient;
    private final SonarConfiguration sonarConfiguration;

    public SonarQubeAdapter(SonarFeignClient sonarFeignClient, SonarConfiguration sonarConfiguration) {
        this.sonarFeignClient = sonarFeignClient;
        this.sonarConfiguration = sonarConfiguration;
    }

    @Override
    public QualityGate getQualityGateStatus(ProjectKey projectKey) {
        logger.debug("Calling SonarQube API for quality gate status, projectKey: {}", projectKey);

        try {
            QualityGateClientResponse response = sonarFeignClient.getQualityGateStatus(projectKey.value());

            if (response == null || response.projectStatus() == null) {
                throw new IllegalStateException("Invalid quality gate response: null or missing project status");
            }

            List<com.mk.sonar.centralizesonar.domain.sonar.model.Condition> domainConditions = response.projectStatus().conditions().stream()
                    .map(this::toDomainCondition)
                    .collect(Collectors.toList());

            return new QualityGate(response.projectStatus().status(), domainConditions);
        } catch (com.mk.sonar.centralizesonar.infrastructure.sonar.exception.ProjectNotFoundException e) {
            throw new com.mk.sonar.centralizesonar.domain.sonar.exception.ProjectNotFoundException(projectKey.value());
        }
    }

    @Override
    public Metrics getMetrics(ProjectKey projectKey) {
        String metricKeys = sonarConfiguration.metricKeys();
        logger.debug("Calling SonarQube API for metrics, projectKey: {}, metricKeys: {}", projectKey, metricKeys);

        try {
            MetricsClientResponse response = sonarFeignClient.getMetrics(projectKey.value(), metricKeys);

            if (response == null || response.component() == null) {
                throw new IllegalStateException("Invalid metrics response: null or missing component");
            }

            List<com.mk.sonar.centralizesonar.domain.sonar.model.Measure> domainMeasures = response.component().measures().stream()
                    .map(this::toDomainMeasure)
                    .collect(Collectors.toList());

            return new Metrics(domainMeasures);
        } catch (com.mk.sonar.centralizesonar.infrastructure.sonar.exception.ProjectNotFoundException e) {
            throw new com.mk.sonar.centralizesonar.domain.sonar.exception.ProjectNotFoundException(projectKey.value());
        }
    }

    @Override
    public ProjectCatalog getProjectCatalog() {
        logger.debug("Calling SonarQube API for project catalog");

        try {
            ProjectCatalogClientResponse response = sonarFeignClient.getProjectCatalog();

            if (response == null || response.components() == null) {
                throw new IllegalStateException("Invalid project catalog response: null or missing components");
            }

            List<ProjectInfo> domainProjects = response.components().stream()
                    .map(this::toDomainProjectInfo)
                    .collect(Collectors.toList());

            return new ProjectCatalog(domainProjects);
        } catch (com.mk.sonar.centralizesonar.infrastructure.sonar.exception.ProjectNotFoundException e) {
            throw new com.mk.sonar.centralizesonar.domain.sonar.exception.ProjectNotFoundException("Failed to fetch project catalog");
        }
    }

    private ProjectInfo toDomainProjectInfo(ProjectComponent component) {
        ProjectKey projectKey = new ProjectKey(component.key());
        return new ProjectInfo(projectKey, component.name());
    }

    private com.mk.sonar.centralizesonar.domain.sonar.model.Condition toDomainCondition(
            Condition clientCondition) {
        return new com.mk.sonar.centralizesonar.domain.sonar.model.Condition(
                clientCondition.metricKey(),
                clientCondition.status(),
                clientCondition.actualValue(),
                clientCondition.errorThreshold()
        );
    }

    private com.mk.sonar.centralizesonar.domain.sonar.model.Measure toDomainMeasure(
            Measure clientMeasure) {
        return new com.mk.sonar.centralizesonar.domain.sonar.model.Measure(
                clientMeasure.metric(),
                clientMeasure.value()
        );
    }
}

