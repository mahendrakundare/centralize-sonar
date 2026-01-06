package com.mk.sonar.centralizesonar.client;

import com.mk.sonar.centralizesonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.client.response.QualityGateClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SonarClient {

    private static final Logger logger = LoggerFactory.getLogger(SonarClient.class);
    private final SonarFeignClient sonarFeignClient;
    private final SonarConfiguration sonarConfiguration;

    public SonarClient(SonarFeignClient sonarFeignClient,
                       SonarConfiguration sonarConfiguration) {
        this.sonarFeignClient = sonarFeignClient;
        this.sonarConfiguration = sonarConfiguration;
    }

    public QualityGateClientResponse getQualityGateStatus(String projectKey) {
        logger.debug("Calling SonarQube API for quality gate status, projectKey: {}", projectKey);
        return sonarFeignClient.getQualityGateStatus(projectKey);
    }

    public MetricsClientResponse getMetrics(String projectKey) {
        String metricKeys = sonarConfiguration.metricKeys();
        logger.debug("Calling SonarQube API for metrics, projectKey: {}, metricKeys: {}", projectKey, metricKeys);
        return sonarFeignClient.getMetrics(projectKey, metricKeys);
    }

}
