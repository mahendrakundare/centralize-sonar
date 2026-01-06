package com.mk.sonar.centralizesonar.client;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SonarConfigurationValidator implements ConstraintValidator<ValidSonarConfiguration, SonarConfiguration> {

    @Override
    public void initialize(ValidSonarConfiguration constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(SonarConfiguration config, ConstraintValidatorContext context) {
        if (config == null) {
            return true; // Let @NotNull handle null checks
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        // If token is not provided, username and password are required
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

