package com.mk.sonar.centralizesonar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "cors")
@Validated
public record CorsProperties (String[] origins) { }