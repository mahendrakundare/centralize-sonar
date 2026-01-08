package com.mk.sonar.centralizesonar.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeignRetryConfiguration.class)
public class FeignGlobalConfig {
}

