# Project README

## Overview

This project is a Java 17 application built with Gradle, using Spring MVC and Jakarta EE imports. It includes Docker
Compose support to spin up a local SonarQube instance for code quality analysis. After bringing up SonarQube, copy your
local Sonar configuration into the application configuration and then you can run and test the API.

## Prerequisites

- Java 17 (JDK)
- Docker and Docker Compose
- Git
- Internet access for dependency resolution

Optional:

- cURL or a REST client (e.g., Postman) to test the API

## Getting Started

1. Clone the repository

    ```bash
    git clone <repository-url>
    cd <repository-directory>
    ```
2. Start SonarQube first (required)

- This repository contains a docker-compose.yml that brings up SonarQube locally.

    ```bash
    docker compose up -d
    ```
- Wait until SonarQube is healthy and accessible (default: [http://localhost:9000](http://localhost:9000)).

3. Configure application.yml for SonarQube

- Copy your SonarQube connection details (URL, token, etc.) into your application configuration.
- Typically you will add values like the server URL and authentication token under your application’s configuration
  section.

``` yaml
# application.yml
# Example placeholders — replace with your actual values
sonar:
  hostUrl: http://localhost:9000
  token: <your-generated-sonarqube-token>
  username: <your-sonarqube-username>
  password: <your-sonarqube-password>
```

- If your project expects a different configuration path or property names, align them accordingly.

4. Build the project

    ```bash
    ./gradlew build
    ```
5. Run the application

- If the project is set up as a Spring Boot application:

    ```bash
    ./gradlew bootRun
    ```
- Otherwise, run the main application class from your IDE or deploy the built artifact to your target servlet container.

6. Test the API

- Once the app is running, use your browser, cURL, or Postman to call the API endpoints (e.g., a health or status
  endpoint if provided).

    ```bash
    curl http://localhost:8080/your-endpoint
    ```

## API Documentation (OpenAPI/Swagger)

This project exposes OpenAPI documentation and an interactive Swagger UI.

- Swagger UI:
    - [http://localhost:8181/swagger-ui.html](http://localhost:8181/swagger-ui.html)
    - [http://localhost:8181/swagger-ui/index.html](http://localhost:8181/swagger-ui/index.html)

- OpenAPI JSON:
    - [http://localhost:8181/v3/api-docs](http://localhost:8181/v3/api-docs)

- OpenAPI YAML:
    - [http://localhost:8181/v3/api-docs.yaml](http://localhost:8181/v3/api-docs.yaml)

## SonarQube Notes

- Default SonarQube URL: [http://localhost:9000](http://localhost:9000)
- First-time login defaults may be required; generate a token from your SonarQube user profile and place it in your
  application.yml as described above.
- Make sure the container is healthy before running analysis or starting the app if the app depends on Sonar configs at
  startup.



