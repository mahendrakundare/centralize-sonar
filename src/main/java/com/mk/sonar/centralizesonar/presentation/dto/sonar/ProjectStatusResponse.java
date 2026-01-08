package com.mk.sonar.centralizesonar.presentation.dto.sonar;

import java.util.List;

public record ProjectStatusResponse(String status, List<ConditionResult> conditions) {
}

