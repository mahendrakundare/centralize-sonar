package com.mk.sonar.centralizesonar.client.response;

import java.util.List;

public class Component {
    private final List<Measure> measures;

    public Component(List<Measure> measures) {
        this.measures = measures;
    }

    public List<Measure> getMeasures() {
        return measures;
    }
}
