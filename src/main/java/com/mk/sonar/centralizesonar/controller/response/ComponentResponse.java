package com.mk.sonar.centralizesonar.controller.response;


import java.util.List;

public record ComponentResponse(List<MeasureResponse> measures) {
}
