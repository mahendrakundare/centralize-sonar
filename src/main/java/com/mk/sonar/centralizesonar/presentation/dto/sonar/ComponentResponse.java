package com.mk.sonar.centralizesonar.presentation.dto.sonar;

import java.util.List;

public record ComponentResponse(List<MeasureResponse> measures) {
}

