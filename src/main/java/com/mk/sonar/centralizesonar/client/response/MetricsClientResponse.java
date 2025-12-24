package com.mk.sonar.centralizesonar.client.response;

public class MetricsClientResponse {
    private final Component component;

    public MetricsClientResponse(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }
}
