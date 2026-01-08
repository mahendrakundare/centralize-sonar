package com.mk.sonar.centralizesonar.infrastructure.sonar.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SonarConfigurationValidator implements ConstraintValidator<ValidSonarConfiguration, SonarConfiguration> {

    @Override
    public void initialize(ValidSonarConfiguration constraintAnnotation) {
    }

    @Override
    public boolean isValid(SonarConfiguration config, ConstraintValidatorContext context) {
        if (config == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (config.token() == null || config.token().isEmpty()) {
            if (config.username() == null || config.username().isEmpty()) {
                context.buildConstraintViolationWithTemplate("Username is required when token is not provided")
                        .addConstraintViolation();
                isValid = false;
            }
            if (config.password() == null || config.password().isEmpty()) {
                context.buildConstraintViolationWithTemplate("Password is required when token is not provided")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}

