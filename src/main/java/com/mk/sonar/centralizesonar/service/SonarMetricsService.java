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

    public QualityGateApiResponse fetchQualityGate() {
        return mapToQualityGateApiResponse(sonarClient.getQualityGateStatus());
    }

    private QualityGateApiResponse mapToQualityGateApiResponse(QualityGateClientResponse qualityGateStatus) {
        return new QualityGateApiResponse(new ProjectStatusResponse(qualityGateStatus.getProjectStatus().getStatus(),
                qualityGateStatus.getProjectStatus().getConditions()
                        .stream().map(condition -> new com.mk.sonar.centralizesonar.controller.response.ConditionResult(
                                condition.getMetricKey(),
                                condition.getStatus(),
                                condition.getActualValue(),
                                condition.getErrorThreshold()
                        )).toList()));
    }


    public MetricsApiResponse fetchMetrics() {
        return mapToMetricsApiResponse(sonarClient.getMetrics());
    }

    private MetricsApiResponse mapToMetricsApiResponse(MetricsClientResponse metrics) {
        return new MetricsApiResponse(new ComponentResponse(
                metrics.getComponent().getMeasures()
                        .stream()
                        .map(measure -> new MeasureResponse(measure.getMetric(), measure.getValue())).toList())
        );
    }

}
