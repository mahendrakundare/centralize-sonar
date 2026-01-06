package com.mk.sonar.centralizesonar.client;

import com.mk.sonar.centralizesonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.client.response.QualityGateClientResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SonarClient {

    private final RestTemplate restTemplate;
    private final SonarConfiguration sonarConfiguration;

    public SonarClient(RestTemplate restTemplate,
                       SonarConfiguration sonarConfiguration) {
        this.restTemplate = restTemplate;
        this.sonarConfiguration = sonarConfiguration;
    }


    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonarConfiguration.getUsername(), sonarConfiguration.getPassword());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    public QualityGateClientResponse getQualityGateStatus(String projectKey) {
        String url = sonarConfiguration.getUrl() + "/api/qualitygates/project_status?projectKey=" + projectKey;

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<QualityGateClientResponse> qualityGateResponseResponse =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        QualityGateClientResponse.class
                );

        return qualityGateResponseResponse.getBody();
    }

    public MetricsClientResponse getMetrics(String projectKey) {
        String metricKeys = "bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,security_hotspots,security_rating,reliability_rating";

        String url = sonarConfiguration.getUrl() + "/api/measures/component?component=" + projectKey + "&metricKeys=" + metricKeys;

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        ResponseEntity<MetricsClientResponse> metricsResponseResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                MetricsClientResponse.class
        );

        return metricsResponseResponse.getBody();
    }

}
