package com.mk.sonar.centralizesonar.infrastructure.sonar.client.response;

import com.fasterxml.jackson.databind.JsonNode;

public record JiraClientResponse(JsonNode changelog) {
}

