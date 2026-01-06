package com.mk.sonar.centralizesonar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.mk.sonar")
@EnableFeignClients(basePackages = "com.mk.sonar.centralizesonar.client")
public class CentralizeSonarApplication {

    public static void main(String[] args) {
        SpringApplication.run(CentralizeSonarApplication.class, args);
    }

}
