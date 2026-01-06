package com.mk.sonar.centralizesonar.client.response;

import java.util.List;

public record ProjectStatus(
        String status,
        List<Condition> conditions
) {
}
