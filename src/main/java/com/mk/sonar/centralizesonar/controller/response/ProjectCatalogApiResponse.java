package com.mk.sonar.centralizesonar.controller.response;

import java.util.List;

public record ProjectCatalogApiResponse(List<ProjectComponentResponse> projects) {
}
