package com.mk.sonar.centralizesonar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.mk.sonar")
public class CentralizeSonarApplication {

    public static void main(String[] args) {
        SpringApplication.run(CentralizeSonarApplication.class, args);
    }

}
