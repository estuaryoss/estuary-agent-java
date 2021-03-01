package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.model.ProcessInfo;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class ProcessApiControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private About about;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private Authentication auth;

    @Test
    public void whenGettingAllTheProcesses_ThenTheListIsNotEmpty() {
        ResponseEntity<ApiResponse<ArrayList<ProcessInfo>>> response = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/processes",
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, new HashMap<>()),
                        new ParameterizedTypeReference<>() {
                        });
        ApiResponse<ArrayList<ProcessInfo>> body = response.getBody();

        Assertions.assertThat(body.getDescription().size()).isGreaterThan(0);
    }

    @Test
    public void whenGettingTheProcessesForExec_ThenTheListIsNotEmpty() {
        final String EXEC = "java";
        ResponseEntity<ApiResponse<ArrayList<ProcessInfo>>> response = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/processes/" + EXEC,
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, new HashMap<>()),
                        new ParameterizedTypeReference<>() {
                        });
        ApiResponse<ArrayList<ProcessInfo>> body = response.getBody();

        Assertions.assertThat(body.getDescription().size()).isGreaterThan(0);
        body.getDescription().forEach(processInfo -> Assertions.assertThat(processInfo.getName()).contains(EXEC));
    }
}
