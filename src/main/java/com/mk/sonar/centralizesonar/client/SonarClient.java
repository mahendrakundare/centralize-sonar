package com.mk.sonar.centralizesonar.client;

import com.mk.sonar.centralizesonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.client.response.QualityGateClientResponse;
import org.springframework.stereotype.Service;

@Service
public class SonarClient {

    private final SonarFeignClient sonarFeignClient;
    private final SonarConfiguration sonarConfiguration;

    public SonarClient(SonarFeignClient sonarFeignClient,
                       SonarConfiguration sonarConfiguration) {
        this.sonarFeignClient = sonarFeignClient;
        this.sonarConfiguration = sonarConfiguration;
    }

    public QualityGateClientResponse getQualityGateStatus(String projectKey) {
        return sonarFeignClient.getQualityGateStatus(projectKey);
    }

    public MetricsClientResponse getMetrics(String projectKey) {
        String metricKeys = sonarConfiguration.metricKeys();
        return sonarFeignClient.getMetrics(projectKey, metricKeys);
    }

}
