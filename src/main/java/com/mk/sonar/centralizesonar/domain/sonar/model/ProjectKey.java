package com.mk.sonar.centralizesonar.domain.sonar.model;

import java.util.Objects;

public record ProjectKey(String value) {
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 400;

    public ProjectKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Project key cannot be null or blank");
        }
        this.value = normalize(value);
        validate();
    }

    private void validate() {
        if (value.isEmpty() || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Project key must be between %d and %d characters", MIN_LENGTH, MAX_LENGTH)
            );
        }
    }

    private String normalize(String key) {
        return key.trim();
    }

    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectKey that = (ProjectKey) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return value;
    }
}

