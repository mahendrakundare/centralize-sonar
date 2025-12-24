package com.mk.sonar.centralizesonar.controller;

import com.mk.sonar.centralizesonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.client.response.QualityGateClientResponse;
import com.mk.sonar.centralizesonar.controller.response.MetricsApiResponse;
import com.mk.sonar.centralizesonar.controller.response.QualityGateApiResponse;
import com.mk.sonar.centralizesonar.service.SonarMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sonar")
public class SonarMetricController {

    private final SonarMetricsService sonarMetricsService;

    public SonarMetricController(SonarMetricsService sonarMetricsService) {
        this.sonarMetricsService = sonarMetricsService;
    }

    @GetMapping("/quality-gate")
    public QualityGateApiResponse fetchQualityGate() {
        return sonarMetricsService.fetchQualityGate();
    }

    @GetMapping("/metrics")
    public MetricsApiResponse fetchMetrics() {
        return sonarMetricsService.fetchMetrics();
    }


}
