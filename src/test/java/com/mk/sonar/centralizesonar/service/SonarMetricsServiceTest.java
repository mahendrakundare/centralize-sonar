package com.mk.sonar.centralizesonar.service;

import com.mk.sonar.centralizesonar.client.SonarClient;
import com.mk.sonar.centralizesonar.client.response.*;
import com.mk.sonar.centralizesonar.controller.response.MetricsApiResponse;
import com.mk.sonar.centralizesonar.controller.response.ProjectCatalogApiResponse;
import com.mk.sonar.centralizesonar.controller.response.QualityGateApiResponse;
import com.mk.sonar.centralizesonar.exception.SonarQubeServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SonarMetricsServiceTest {
    @InjectMocks
    private SonarMetricsService service;

    @Mock
    private SonarClient sonarClient;


    @Test
    void fetchQualityGate_whenClientReturnsNull_throwsServiceException() {
        when(sonarClient.getQualityGateStatus("proj-key")).thenReturn(null);

        SonarQubeServiceException ex =
                assertThrows(SonarQubeServiceException.class, () -> service.fetchQualityGate("proj-key"));
        assertEquals(500, ex.statusCode());
        verify(sonarClient).getQualityGateStatus("proj-key");
    }

    @Test
    void fetchQualityGate_whenProjectStatusIsNull_throwsServiceException() {
        QualityGateClientResponse resp = mock(QualityGateClientResponse.class);
        when(resp.projectStatus()).thenReturn(null);
        when(sonarClient.getQualityGateStatus("proj-key")).thenReturn(resp);

        SonarQubeServiceException ex =
                assertThrows(SonarQubeServiceException.class, () -> service.fetchQualityGate("proj-key"));
        assertEquals(500, ex.statusCode());
        verify(sonarClient).getQualityGateStatus("proj-key");
    }

    @Test
    void fetchQualityGate_whenValid_returnsMappedResponse() {
        QualityGateClientResponse resp = new QualityGateClientResponse(new ProjectStatus("OK", List.of(new Condition("new_coverage", "ERROR", "75.0", "80.0"))));
        when(sonarClient.getQualityGateStatus("proj-key")).thenReturn(resp);

        QualityGateApiResponse result = service.fetchQualityGate("proj-key");

        assertNotNull(result);
        assertNotNull(result.projectStatus());
        assertEquals("OK", result.projectStatus().status());
        assertNotNull(result.projectStatus().conditions());
        verify(sonarClient, times(1)).getQualityGateStatus("proj-key");

    }

    @Test
    void fetchMetrics_whenClientReturnsNull_throwsServiceException() {
        when(sonarClient.getMetrics("proj-key")).thenReturn(null);

        SonarQubeServiceException ex =
                assertThrows(SonarQubeServiceException.class, () -> service.fetchMetrics("proj-key"));
        assertEquals(500, ex.statusCode());
        verify(sonarClient).getMetrics("proj-key");
    }

    @Test
    void fetchMetrics_whenComponentIsNull_throwsServiceException() {
        MetricsClientResponse resp = mock(MetricsClientResponse.class);
        when(resp.component()).thenReturn(null);
        when(sonarClient.getMetrics("proj-key")).thenReturn(resp);

        SonarQubeServiceException ex =
                assertThrows(SonarQubeServiceException.class, () -> service.fetchMetrics("proj-key"));
        assertEquals(500, ex.statusCode());
        verify(sonarClient).getMetrics("proj-key");
    }

    @Test
    void fetchMetrics_whenValid_returnsMappedResponse() {
        MetricsClientResponse resp = mock(MetricsClientResponse.class, RETURNS_DEEP_STUBS);
        when(resp.component().measures()).thenReturn(List.of()); // empty list is fine
        when(sonarClient.getMetrics("proj-key")).thenReturn(resp);

        MetricsApiResponse result = service.fetchMetrics("proj-key");

        assertNotNull(result);
        assertNotNull(result.component());
        assertNotNull(result.component().measures());
        assertTrue(result.component().measures().isEmpty());
        verify(sonarClient).getMetrics("proj-key");
    }

    @Test
    void fetchProjectCatalog_whenClientReturnsNull_throwsServiceException() {
        when(sonarClient.getProjectCatalog()).thenReturn(null);

        SonarQubeServiceException ex =
                assertThrows(SonarQubeServiceException.class, service::fetchProjectCatalog);
        assertEquals(500, ex.statusCode());
        verify(sonarClient).getProjectCatalog();
    }

    @Test
    void fetchProjectCatalog_whenComponentsNull_throwsServiceException() {
        ProjectCatalogClientResponse resp = mock(ProjectCatalogClientResponse.class);
        when(resp.components()).thenReturn(null);
        when(sonarClient.getProjectCatalog()).thenReturn(resp);

        SonarQubeServiceException ex =
                assertThrows(SonarQubeServiceException.class, service::fetchProjectCatalog);
        assertEquals(500, ex.statusCode());
        verify(sonarClient).getProjectCatalog();
    }

    @Test
    void fetchProjectCatalog_whenValid_returnsMappedResponse() {
        ProjectCatalogClientResponse resp = new ProjectCatalogClientResponse(List.of(new ProjectComponent("proj-1", "Project One")));
        when(sonarClient.getProjectCatalog()).thenReturn(resp);

        ProjectCatalogApiResponse result = service.fetchProjectCatalog();

        assertNotNull(result);
        assertNotNull(result.projects());
        verify(sonarClient, times(1)).getProjectCatalog();
    }

}