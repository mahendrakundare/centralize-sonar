package com.mk.sonar.centralizesonar.infrastructure.sonar.adapter;

import com.mk.sonar.centralizesonar.domain.sonar.model.*;
import com.mk.sonar.centralizesonar.domain.sonar.port.JiraPort;
import com.mk.sonar.centralizesonar.infrastructure.sonar.client.feign.JiraFeignClient;
import com.mk.sonar.centralizesonar.infrastructure.sonar.config.SonarConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class JiraAdapter implements JiraPort {

    private static final Logger logger = LoggerFactory.getLogger(JiraAdapter.class);
    private final JiraFeignClient jiraFeignClient;

    public JiraAdapter(JiraFeignClient jiraFeignClient, SonarConfiguration sonarConfiguration) {
        this.jiraFeignClient = jiraFeignClient;
    }

    @Override
    public JsonNode getProjectStatus(ProjectKey projectKey) {
        logger.debug("Calling Jira API for project status, projectKey: {}", projectKey);

        try {
            String jql = URLEncoder.encode(
                    "project = " + projectKey.value() + " AND status = Done AND resolved >= -180d",
                    StandardCharsets.UTF_8
            );
            JsonNode response = jiraFeignClient.getProjectStatus(jql, "changelog", "created,resolutiondate,changelog");
            if (response == null) {
                throw new IllegalStateException("Invalid project status response: null or missing project status");
            }

            return response;

        } catch (com.mk.sonar.centralizesonar.infrastructure.sonar.exception.ProjectNotFoundException e) {
            throw new com.mk.sonar.centralizesonar.domain.sonar.exception.ProjectNotFoundException(projectKey.value());
        }
    }
}

