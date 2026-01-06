package com.mk.sonar.centralizesonar.service;

import com.mk.sonar.centralizesonar.client.SonarClient;
import com.mk.sonar.centralizesonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.client.response.QualityGateClientResponse;
import com.mk.sonar.centralizesonar.controller.response.*;
import com.mk.sonar.centralizesonar.exception.SonarQubeServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SonarMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(SonarMetricsService.class);
    private final SonarClient sonarClient;

    public SonarMetricsService(SonarClient sonarClient) {
        this.sonarClient = sonarClient;
    }

    public QualityGateApiResponse fetchQualityGate(String projectKey) {
        logger.debug("Fetching quality gate for project: {}", projectKey);
        QualityGateClientResponse response = sonarClient.getQualityGateStatus(projectKey);
        logger.debug("Successfully fetched quality gate for project: {}", projectKey);
        return mapToQualityGateApiResponse(response);
    }

    private QualityGateApiResponse mapToQualityGateApiResponse(QualityGateClientResponse qualityGateStatus) {
        if (qualityGateStatus == null || qualityGateStatus.projectStatus() == null) {
            logger.error("Invalid quality gate response: null or missing project status");
            throw new SonarQubeServiceException("Invalid response from SonarQube", 500);
        }

        return new QualityGateApiResponse(new ProjectStatusResponse(
                qualityGateStatus.projectStatus().status(),
                qualityGateStatus.projectStatus().conditions()
                        .stream().map(condition -> new ConditionResult(
                                condition.metricKey(),
                                condition.status(),
                                condition.actualValue(),
                                condition.errorThreshold()
                        )).toList()));
    }


    public MetricsApiResponse fetchMetrics(String projectKey) {
        logger.debug("Fetching metrics for project: {}", projectKey);
        MetricsClientResponse response = sonarClient.getMetrics(projectKey);
        logger.debug("Successfully fetched metrics for project: {}", projectKey);
        return mapToMetricsApiResponse(response);
    }

    private MetricsApiResponse mapToMetricsApiResponse(MetricsClientResponse metrics) {
        if (metrics == null || metrics.component() == null) {
            logger.error("Invalid metrics response: null or missing component");
            throw new SonarQubeServiceException("Invalid response from SonarQube", 500);
        }

        return new MetricsApiResponse(new ComponentResponse(
                metrics.component().measures()
                        .stream()
                        .map(measure -> new MeasureResponse(measure.metric(), measure.value()))
                        .toList())
        );
    }

}
