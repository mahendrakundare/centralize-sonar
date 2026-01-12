package com.mk.sonar.centralizesonar.application.sonar.service;

import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectJiraStatus;
import com.mk.sonar.centralizesonar.domain.sonar.model.ProjectKey;
import com.mk.sonar.centralizesonar.domain.sonar.port.JiraPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class JiraService {

    private static final Logger logger = LoggerFactory.getLogger(JiraService.class);
    private final JiraPort jiraPort;

    public JiraService(JiraPort jiraPort) {
        this.jiraPort = jiraPort;
    }

    static ZonedDateTime parseJiraDate(String date) {
        return ZonedDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    }

    public static String calculate(ZonedDateTime start, ZonedDateTime end) {
        Duration duration = Duration.between(start, end);

        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();

        return days + " days " + hours + " hours";
    }

    public ProjectJiraStatus fetchProjectStatus(String projectKeyString) {
        logger.debug("Fetching project status: {}", projectKeyString);
        ProjectKey projectKey = new ProjectKey(projectKeyString);
        JsonNode projectStatus = jiraPort.getProjectStatus(projectKey);
        logger.debug("Successfully fetched project status: {}", projectStatus);

        JsonNode fields = projectStatus.get("fields");
        ZonedDateTime createdDate = parseJiraDate(fields.get("created").asText());
        ZonedDateTime resolvedDate = parseJiraDate(fields.get("resolutiondate").asText());
        String leadTime = calculate(createdDate, resolvedDate);

        ZonedDateTime inProgressDate = null;
        JsonNode changelog = projectStatus.get("changelog");

        for (JsonNode history : changelog.get("histories")) {
            for (JsonNode item : history.get("items")) {
                if ("status".equals(item.get("field").asText()) &&
                        "In Progress".equals(item.get("fromString").asText())) {

                    inProgressDate = parseJiraDate(history.get("created").asText());
                    break;
                }
            }
            if (inProgressDate != null) break;
        }
        String cycleTime = calculate(inProgressDate, resolvedDate);

        return new ProjectJiraStatus(projectKey, leadTime, cycleTime);
    }
}

