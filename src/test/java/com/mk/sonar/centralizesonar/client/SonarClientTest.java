package com.mk.sonar.centralizesonar.client;

import com.mk.sonar.centralizesonar.client.response.MetricsClientResponse;
import com.mk.sonar.centralizesonar.client.response.ProjectCatalogClientResponse;
import com.mk.sonar.centralizesonar.client.response.QualityGateClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SonarClientTest {

    @Mock
    private SonarFeignClient sonarFeignClient;

    @Mock
    private SonarConfiguration sonarConfiguration;

    @InjectMocks
    private SonarClient sonarClient;

    @Test
    void getQualityGateStatus_delegatesToFeignClient() {
        String projectKey = "proj-key";
        QualityGateClientResponse expected = org.mockito.Mockito.mock(QualityGateClientResponse.class);
        when(sonarFeignClient.getQualityGateStatus(eq(projectKey))).thenReturn(expected);

        QualityGateClientResponse result = sonarClient.getQualityGateStatus(projectKey);

        assertSame(expected, result);
        verify(sonarFeignClient).getQualityGateStatus(eq(projectKey));
    }

    @Test
    void getMetrics_usesMetricKeysFromConfiguration_andDelegatesToFeignClient() {
        String projectKey = "proj-key";
        String metricKeys = "bugs,coverage";
        MetricsClientResponse expected = org.mockito.Mockito.mock(MetricsClientResponse.class);

        when(sonarConfiguration.metricKeys()).thenReturn(metricKeys);
        when(sonarFeignClient.getMetrics(eq(projectKey), eq(metricKeys))).thenReturn(expected);

        MetricsClientResponse result = sonarClient.getMetrics(projectKey);

        assertSame(expected, result);
        verify(sonarConfiguration).metricKeys();
        verify(sonarFeignClient).getMetrics(eq(projectKey), eq(metricKeys));
    }

    @Test
    void getProjectCatalog_delegatesToFeignClient() {
        ProjectCatalogClientResponse expected = org.mockito.Mockito.mock(ProjectCatalogClientResponse.class);
        when(sonarFeignClient.getProjectCatalog()).thenReturn(expected);

        ProjectCatalogClientResponse result = sonarClient.getProjectCatalog();

        assertSame(expected, result);
        verify(sonarFeignClient).getProjectCatalog();
    }
}