package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import com.github.estuaryoss.agent.repository.ActiveCommandRepository;
import com.github.estuaryoss.agent.repository.FinishedCommandRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class CommandApiControllerRepoTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Authentication auth;

    @Autowired
    private ActiveCommandRepository activeCommandRepository;

    @Autowired
    private FinishedCommandRepository finishedCommandRepository;

    @BeforeEach
    public void cleanRepos() {
        finishedCommandRepository.deleteAll();
        activeCommandRepository.deleteAll();
    }

    @Test
    public void whenSendingSomeCommandsTheReposWillHaveTheCorrectNumberOfEntries() {
        String commands = "echo 1 && echo 2\nls -lrt";

        postCommands(commands);

        assertThat(finishedCommandRepository.findAll().size()).isEqualTo(commands.split("\n").length);
        assertThat(activeCommandRepository.findAll().size()).isEqualTo(0);
    }

    private ResponseEntity<ApiResponse<CommandDescription>> postCommands(String command) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/command",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers),
                        new ParameterizedTypeReference<>() {
                        });
    }
}
