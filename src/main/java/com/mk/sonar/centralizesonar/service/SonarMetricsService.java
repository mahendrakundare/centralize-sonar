package com.mk.sonar.centralizesonar.service;

import com.mk.sonar.centralizesonar.client.SonarClient;
import com.mk.sonar.centralizesonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.client.response.QualityGateClientResponse;
import com.mk.sonar.centralizesonar.controller.response.*;
import org.springframework.stereotype.Service;

@Service
public class SonarMetricsService {

    private final SonarClient sonarClient;

    public SonarMetricsService(SonarClient sonarClient) {
        this.sonarClient = sonarClient;
    }

    public QualityGateApiResponse fetchQualityGate(String projectKey) {
        return mapToQualityGateApiResponse(sonarClient.getQualityGateStatus(projectKey));
    }

    private QualityGateApiResponse mapToQualityGateApiResponse(QualityGateClientResponse qualityGateStatus) {
        return new QualityGateApiResponse(new ProjectStatusResponse(qualityGateStatus.projectStatus().status(),
                qualityGateStatus.projectStatus().conditions()
                        .stream().map(condition -> new com.mk.sonar.centralizesonar.controller.response.ConditionResult(
                                condition.metricKey(),
                                condition.status(),
                                condition.actualValue(),
                                condition.errorThreshold()
                        )).toList()));
    }


    public MetricsApiResponse fetchMetrics(String projectKey) {
        return mapToMetricsApiResponse(sonarClient.getMetrics(projectKey));
    }

    private MetricsApiResponse mapToMetricsApiResponse(MetricsClientResponse metrics) {
        return new MetricsApiResponse(new ComponentResponse(
                metrics.component().measures()
                        .stream()
                        .map(measure -> new MeasureResponse(measure.metric(), measure.value())).toList())
        );
    }

}
