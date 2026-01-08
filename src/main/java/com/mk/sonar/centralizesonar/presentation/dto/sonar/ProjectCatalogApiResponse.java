package com.mk.sonar.centralizesonar.presentation.dto.sonar;

import java.util.List;

public record ProjectCatalogApiResponse(List<ProjectComponentResponse> projects) {
}

