package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.model.ExecutionStatus;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import com.github.estuaryoss.agent.repository.CommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    private CommandRepository commandRepository;

    @BeforeEach
    public void cleanRepos() {
        commandRepository.deleteAll();
    }

    @Test
    public void whenSendingSomeCommandsTheReposWillHaveTheCorrectNumberOfEntries() {
        String commands = "echo 1 && echo 2\necho 3";

        postCommands(commands);

        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.RUNNING.getStatus()).size()).isEqualTo(0);
        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.FINISHED.getStatus()).size()).isEqualTo(commands.split("\n").length);
    }

    @Test
    public void whenSendingSomeCommandsThenSomeOfThemWillBeQueued() throws InterruptedException {
        String commands = "echo 1\nsleep 1\nsleep 2";
//        String commands = "echo 1\nping -n 2 127.0.0.1\nping -n 3 127.0.0.1";

        CompletableFuture.runAsync(() -> {
            postCommands(commands);
        });

        Thread.sleep(1000);

//        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.RUNNING.getStatus()).size()).isEqualTo(1);
//        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.FINISHED.getStatus()).size()).isEqualTo(1);
        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.QUEUED.getStatus()).size()).isGreaterThanOrEqualTo(1);

        Thread.sleep(4000);
        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.FINISHED.getStatus()).size()).isEqualTo(commands.split("\n").length);
    }

    private ResponseEntity<ApiResponse<CommandDescription>> postCommands(String command) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/commands",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers),
                        new ParameterizedTypeReference<>() {
                        });
    }
}
