package com.mk.sonar.centralizesonar.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sonar")
public class SonarConfiguration {
    private final String url;
    private final String token;
    private final String username;
    private final String password;

    public SonarConfiguration(String url, String token, String username, String password) {
        this.url = url;
        this.token = token;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
