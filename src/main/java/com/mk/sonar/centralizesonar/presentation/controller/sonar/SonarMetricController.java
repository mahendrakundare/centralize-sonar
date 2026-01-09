package com.mk.sonar.centralizesonar.presentation.controller.sonar;

import com.mk.sonar.centralizesonar.application.sonar.service.MetricsService;
import com.mk.sonar.centralizesonar.application.sonar.service.QualityGateService;
import com.mk.sonar.centralizesonar.domain.sonar.model.*;
import com.mk.sonar.centralizesonar.presentation.dto.sonar.*;
import com.mk.sonar.centralizesonar.presentation.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sonar")
@Validated
public class SonarMetricController {

    private final QualityGateService qualityGateService;
    private final MetricsService metricsService;

    public SonarMetricController(QualityGateService qualityGateService, MetricsService metricsService) {
        this.qualityGateService = qualityGateService;
        this.metricsService = metricsService;
    }

    @Operation(
            summary = "Get quality gate status",
            description = "Fetches the current Quality Gate status and related details from Sonar for the specified project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quality Gate status successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QualityGateApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid project key",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/quality-gate")
    public QualityGateApiResponse fetchQualityGate(
            @Parameter(description = "SonarQube project key", required = true)
            @RequestParam
            @NotBlank(message = "Project key cannot be blank")
            @Size(min = 1, max = 400, message = "Project key must be between 1 and 400 characters")
            String projectKey) {

        QualityGate qualityGate = qualityGateService.fetchQualityGate(projectKey);
        return toQualityGateApiResponse(qualityGate);
    }

    @Operation(
            summary = "Get Sonar metrics",
            description = "Fetches aggregated Sonar metrics for the specified project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Metrics successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MetricsApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid project key",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/metrics")
    public MetricsApiResponse fetchMetrics(
            @Parameter(description = "SonarQube project key", required = true)
            @RequestParam
            @NotBlank(message = "Project key cannot be blank")
            @Size(min = 1, max = 400, message = "Project key must be between 1 and 400 characters")
            String projectKey) {

        Metrics metrics = metricsService.fetchMetrics(projectKey);
        return toMetricsApiResponse(metrics);
    }

    private QualityGateApiResponse toQualityGateApiResponse(QualityGate qualityGate) {
        List<ConditionResult> conditions = qualityGate.conditions().stream()
                .map(this::toConditionResult)
                .toList();

        ProjectStatusResponse projectStatus = new ProjectStatusResponse(
                qualityGate.status(),
                conditions
        );

        return new QualityGateApiResponse(projectStatus);
    }

    @Operation(
            summary = "Get List of Projects",
            description = "Fetches the list of all projects from SonarQube"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Projects successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectCatalogApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid project key",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/projects")
    public ProjectCatalogApiResponse fetchProjectCatalog() {
        ProjectCatalog projectCatalog = metricsService.fetchProjectCatalog();
        return toProjectCatalogApiResponse(projectCatalog);
    }

    private ProjectCatalogApiResponse toProjectCatalogApiResponse(ProjectCatalog projectCatalog) {
        List<ProjectComponentResponse> projects = projectCatalog.projects().stream()
                .map(projectInfo -> new ProjectComponentResponse(
                        projectInfo.projectKey().value(),
                        projectInfo.projectName()))
                .toList();
        return new ProjectCatalogApiResponse(projects);
    }

    private ConditionResult toConditionResult(Condition condition) {
        return new ConditionResult(
                condition.metricKey(),
                condition.status(),
                condition.actualValue(),
                condition.errorThreshold()
        );
    }

    private MetricsApiResponse toMetricsApiResponse(Metrics metrics) {
        List<MeasureResponse> measures = metrics.measures().stream()
                .map(this::toMeasureResponse)
                .toList();

        ComponentResponse component = new ComponentResponse(measures);
        return new MetricsApiResponse(component);
    }

    private MeasureResponse toMeasureResponse(Measure measure) {
        return new MeasureResponse(measure.metric(), measure.value());
    }
}

