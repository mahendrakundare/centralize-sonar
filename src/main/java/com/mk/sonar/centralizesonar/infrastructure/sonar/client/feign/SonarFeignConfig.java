package com.mk.sonar.centralizesonar.infrastructure.sonar.client.feign;

import com.mk.sonar.centralizesonar.config.FeignRetryConfiguration;
import com.mk.sonar.centralizesonar.infrastructure.sonar.config.SonarConfiguration;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.mk.sonar.centralizesonar.presentation.filter.CorrelationIdConstants.HEADER_NAME;
import static com.mk.sonar.centralizesonar.presentation.filter.CorrelationIdConstants.MDC_KEY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@EnableConfigurationProperties(SonarConfiguration.class)
public class SonarFeignConfig {

    @Bean
    public RequestInterceptor sonarRequestInterceptor(SonarConfiguration sonarConfiguration) {
        return template -> {
            String token = sonarConfiguration.token();
            if (token == null || token.isEmpty()) {
                String auth = sonarConfiguration.username() + ":" + sonarConfiguration.password();
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
                template.header(AUTHORIZATION, "Basic " + encodedAuth);
            } else {
                template.header(AUTHORIZATION, "Bearer " + token);
            }
            template.header(HttpHeaders.ACCEPT, "application/json");

            String correlationId = MDC.get(MDC_KEY);
            if (correlationId != null && !correlationId.isBlank()) {
                template.header(HEADER_NAME, correlationId);
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new SonarErrorDecoder();
    }

    @Bean
    public Retryer retryer(FeignRetryConfiguration retryConfig) {
        return new SonarFeignRetryer(retryConfig);
    }
}

