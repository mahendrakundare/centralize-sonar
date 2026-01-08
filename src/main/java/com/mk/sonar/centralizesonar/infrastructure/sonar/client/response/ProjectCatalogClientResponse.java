package com.mk.sonar.centralizesonar.infrastructure.sonar.client.response;

import java.util.List;

public record ProjectCatalogClientResponse(List<ProjectComponent> components) {
}

