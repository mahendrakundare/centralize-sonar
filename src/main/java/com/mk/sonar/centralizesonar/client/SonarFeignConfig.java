package com.mk.sonar.centralizesonar.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@EnableConfigurationProperties(FeignRetryConfiguration.class)
public class SonarFeignConfig {

    @Bean
    public RequestInterceptor sonarRequestInterceptor(SonarConfiguration sonarConfiguration) {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                String token = sonarConfiguration.token();
                if (token == null || token.isEmpty()) {
                    String auth = sonarConfiguration.username() + ":" + sonarConfiguration.password();
                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
                    template.header(AUTHORIZATION, "Basic " + encodedAuth);
                } else {
                    template.header(AUTHORIZATION, "Bearer " + token);
                }
                template.header(HttpHeaders.ACCEPT, "application/json");
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

