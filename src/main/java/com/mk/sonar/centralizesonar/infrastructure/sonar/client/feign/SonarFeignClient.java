package com.mk.sonar.centralizesonar.infrastructure.sonar.client.feign;

import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.ProjectCatalogClientResponse;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.response.QualityGateClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "sonarClient",
        url = "${sonar.url}",
        configuration = SonarFeignConfig.class
)
public interface SonarFeignClient {

    @GetMapping("/api/qualitygates/project_status")
    QualityGateClientResponse getQualityGateStatus(@RequestParam("projectKey") String projectKey);

    @GetMapping("/api/measures/component")
    MetricsClientResponse getMetrics(
            @RequestParam("component") String component,
            @RequestParam("metricKeys") String metricKeys
    );

    @GetMapping("/api/projects/search")
    ProjectCatalogClientResponse getProjectCatalog();
}

