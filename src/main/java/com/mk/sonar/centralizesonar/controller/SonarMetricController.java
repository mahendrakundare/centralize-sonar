package com.mk.sonar.centralizesonar.controller;

import com.mk.sonar.centralizesonar.controller.response.ErrorResponse;
import com.mk.sonar.centralizesonar.controller.response.MetricsApiResponse;
import com.mk.sonar.centralizesonar.controller.response.QualityGateApiResponse;
import com.mk.sonar.centralizesonar.service.SonarMetricsService;
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


@RestController
@RequestMapping("/api/v1/sonar")
@Validated
public class SonarMetricController {

    private final SonarMetricsService sonarMetricsService;

    public SonarMetricController(SonarMetricsService sonarMetricsService) {
        this.sonarMetricsService = sonarMetricsService;
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
        return sonarMetricsService.fetchQualityGate(projectKey);
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
        return sonarMetricsService.fetchMetrics(projectKey);
    }


}
