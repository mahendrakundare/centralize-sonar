package com.mk.sonar.centralizesonar.infrastructure.sonar.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.databind.JsonNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@FeignClient(
        name = "jiraClient",
        url = "${jira.url}",
        configuration = JiraFeignConfig.class
)
public interface JiraFeignClient {

    @GetMapping("/rest/api/3/search")
    JsonNode getProjectStatus(@RequestParam("jql") String jql, @RequestParam("expand") String changelog,
                              @RequestParam("fields") String fields);

}

