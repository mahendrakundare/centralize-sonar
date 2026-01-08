package com.mk.sonar.centralizesonar.infrastructure.sonar.client.response;

import java.util.List;

public record ProjectStatus(
        String status,
        List<Condition> conditions
) {
}

