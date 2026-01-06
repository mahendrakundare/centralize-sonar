package com.mk.sonar.centralizesonar.client;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SonarConfigurationValidator.class)
@Documented
public @interface ValidSonarConfiguration {
    String message() default "Invalid SonarQube configuration";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

