package com.mk.sonar.centralizesonar.infrastructure.sonar.client.response;

import java.time.LocalDateTime;

public class JiraMetrics {
    String key;
    LocalDateTime doneDate;
    long leadTimeHours;
    long cycleTimeHours;
}


