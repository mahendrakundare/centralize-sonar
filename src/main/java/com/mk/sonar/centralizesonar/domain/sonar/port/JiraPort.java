package com.mk.sonar.centralizesonar.domain.sonar.port;

import com.mk.sonar.centralizesonar.domain.sonar.model.*;
import tools.jackson.databind.JsonNode;

public interface JiraPort {

    JsonNode getProjectStatus(ProjectKey projectKey);
}

