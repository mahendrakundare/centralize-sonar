package com.mk.sonar.centralizesonar.controller.response;

import java.util.List;

public record ProjectStatusResponse(String status, List<ConditionResult> conditions) {
}
