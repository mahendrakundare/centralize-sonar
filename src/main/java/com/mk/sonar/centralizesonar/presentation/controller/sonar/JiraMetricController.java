package com.mk.sonar.centralizesonar.presentation.controller.sonar;

import com.mk.sonar.centralizesonar.application.sonar.service.JiraService;
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

@RestController
@RequestMapping("/api/v1/jira")
@Validated
public class JiraMetricController {

    private final JiraService jiraService;

    public JiraMetricController(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @Operation(
            summary = "Get project status",
            description = "Fetches the project status and related details from Jira for the specified project"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project status successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JiraStatusApiResponse.class))
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
    @GetMapping("/project-status")
    public JiraStatusApiResponse fetchProjectStatus(
            @Parameter(description = "Jira project key", required = true)
            @RequestParam
            @NotBlank(message = "Project key cannot be blank")
            @Size(min = 1, max = 400, message = "Project key must be between 1 and 400 characters")
            String projectKey) {

        ProjectJiraStatus projectJiraStatus = jiraService.fetchProjectStatus(projectKey);
        System.out.println(projectJiraStatus);
        return toJiraStatusApiResponse(projectJiraStatus);
    }

    private JiraStatusApiResponse toJiraStatusApiResponse(ProjectJiraStatus projectJiraStatus) {
        return new JiraStatusApiResponse(projectJiraStatus.leadTime(), projectJiraStatus.cycleTime());
    }
}

