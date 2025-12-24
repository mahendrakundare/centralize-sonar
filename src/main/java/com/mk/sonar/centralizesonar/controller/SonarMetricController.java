package com.mk.sonar.centralizesonar.controller;

import com.mk.sonar.centralizesonar.controller.response.MetricsApiResponse;
import com.mk.sonar.centralizesonar.controller.response.QualityGateApiResponse;
import com.mk.sonar.centralizesonar.service.SonarMetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api/v1/sonar")
public class SonarMetricController {

    private final SonarMetricsService sonarMetricsService;

    public SonarMetricController(SonarMetricsService sonarMetricsService) {
        this.sonarMetricsService = sonarMetricsService;
    }

    @Operation(
            summary = "Get quality gate status",
            description = "Fetches the current Quality Gate status and related details from Sonar"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quality Gate status successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QualityGateApiResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })

    @GetMapping("/quality-gate")
    public QualityGateApiResponse fetchQualityGate() {
        return sonarMetricsService.fetchQualityGate();
    }

    @Operation(
            summary = "Get Sonar metrics",
            description = "Fetches aggregated Sonar metrics for the configured project(s)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Metrics successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MetricsApiResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })

    @GetMapping("/metrics")
    public MetricsApiResponse fetchMetrics() {
        return sonarMetricsService.fetchMetrics();
    }


}
