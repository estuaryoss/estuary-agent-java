package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.entity.Command;
import com.github.estuaryoss.agent.model.ExecutionStatus;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import com.github.estuaryoss.agent.repository.CommandRepository;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class CommandApiControllerInBackgroundTest {
    private final static String SERVER_PREFIX = "http://localhost:";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();
    @LocalServerPort
    private int port;
    @Autowired
    private HttpRequestUtils httpRequestUtils;
    @Autowired
    private Authentication auth;

    @Autowired
    private CommandRepository commandRepository;

    @BeforeEach
    public void cleanRepos() {
        commandRepository.deleteAll();
    }

    @Test
    public void whenSendingBackgroundCommandThenTheUserMustPollActiveAndFinishedEndpointsForDetails() throws JsonProcessingException {
        String stdOut = "1";
        String command = "echo " + stdOut;

        postCommands(command);
        ResponseEntity<ApiResponse<List<Command>>> requestEntity = getFinishedCommands();
        ApiResponse<List<Command>> body = requestEntity.getBody();
        List<Command> commandList = body.getDescription();

        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.FINISHED.getStatus()).size()).isEqualTo(1);
        assertThat(commandList.size()).isEqualTo(1);
        assertThat(objectMapper.writeValueAsString(commandList))
                .isEqualTo(objectMapper.writeValueAsString(commandRepository.findCommandByStatus(ExecutionStatus.FINISHED.getStatus())));
        assertThat(commandList.get(0).getCommand()).isEqualTo(command);
        assertThat(commandList.get(0).getCode()).isEqualTo(0);
        assertThat(commandList.get(0).getOut().trim()).isEqualTo(stdOut);
        assertThat(commandList.get(0).getErr()).isEqualTo("");
        assertThat(commandList.get(0).getPid()).isGreaterThan(0);
        assertThat(commandList.get(0).getDuration()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void whenSendingBackgroundCommandAndDeleteTheCommandByPidThenTheCommandGetsDeletedFromCommandRepo() throws InterruptedException {
        String command = "tail -f /etc/hostname";
//        String command = "notepad"; //win

        CompletableFuture.runAsync(() -> {
            postCommands(command);
        });

        Thread.sleep(1000);

        ResponseEntity<ApiResponse<List<Command>>> requestEntityActiveCmds = getRunningCommands();
        ApiResponse<List<Command>> body = requestEntityActiveCmds.getBody();
        List<Command> CommandList = body.getDescription();

        ResponseEntity<ApiResponse<List<Command>>> requestEntityFinishedCmd = getFinishedCommands();
        ApiResponse<List<Command>> bodyFinishedCmd = requestEntityFinishedCmd.getBody();
        List<Command> commandList = bodyFinishedCmd.getDescription();

        assertThat(CommandList.size()).isEqualTo(1);
        assertThat(commandList.size()).isEqualTo(0);

        ResponseEntity<ApiResponse<List<Command>>> requestEntityDelete = deleteCommand(CommandList.get(0).getPid());
        ApiResponse<List<Command>> bodyDelete = requestEntityDelete.getBody();

        assertThat(bodyDelete.getDescription().size()).isEqualTo(0);
        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.RUNNING.getStatus()).size()).isEqualTo(0);
        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.FINISHED.getStatus()).size()).isEqualTo(1);
    }

    @Test
    public void whenSendingBackgroundCommandAndDeleteAllCommandsThenAllTheCommandsGetsDeletedFromCommandRepo() throws InterruptedException {
        String command = "tail -f /etc/hostname";
//        String command = "notepad"; //win

        CompletableFuture.runAsync(() -> {
            postCommands(command);
        });

        Thread.sleep(1000);

        ResponseEntity<ApiResponse<List<Command>>> requestEntityActiveCmds = getRunningCommands();
        ApiResponse<List<Command>> body = requestEntityActiveCmds.getBody();
        List<Command> CommandList = body.getDescription();

        ResponseEntity<ApiResponse<List<Command>>> requestEntityFinishedCmd = getFinishedCommands();
        ApiResponse<List<Command>> bodyFinishedCmd = requestEntityFinishedCmd.getBody();
        List<Command> commandList = bodyFinishedCmd.getDescription();

        assertThat(CommandList.size()).isEqualTo(1);
        assertThat(commandList.size()).isEqualTo(0);

        ResponseEntity<ApiResponse<List<Command>>> requestEntityDelete = deleteCommands();
        ApiResponse<List<Command>> bodyDelete = requestEntityDelete.getBody();

        assertThat(bodyDelete.getDescription().size()).isEqualTo(0);
        assertThat(commandRepository.findAll().size()).isEqualTo(1);
        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.RUNNING.getStatus()).size()).isEqualTo(0);
        assertThat(commandRepository.findCommandByStatus(ExecutionStatus.FINISHED.getStatus()).size()).isEqualTo(1);
    }

    @Test
    public void whenSendingMultipleCommandsThenTheRunningOneItsAlwaysInFrontOfTheList() throws InterruptedException {
        String commands = "echo 1\ntail -f /etc/hostname";
//        String commands = "echo 1\nnotepad"; //win

        CompletableFuture.runAsync(() -> {
            postCommands(commands);
        });

        Thread.sleep(1000);

        ResponseEntity<ApiResponse<List<Command>>> responseResponseEntity = getAllCommands();
        ApiResponse<List<Command>> body = responseResponseEntity.getBody();
        List<Command> commandList = body.getDescription();
        String[] commandsSplitFromString = commands.split("\n");
        assertThat(commandList.size()).isEqualTo(commandsSplitFromString.length);

        deleteCommands(); //we already took a response prior to delete the commands

        assertThat(commandList.get(0).getCommand()).isEqualTo(commandsSplitFromString[1]);
        assertThat(commandList.get(1).getCommand()).isEqualTo(commandsSplitFromString[0]);
    }

    private ResponseEntity<ApiResponse<List<Command>>> deleteCommand(long pid) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getEncodedAuthHeader());

        HttpEntity httpEntity = httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers);

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commands/" + pid,
                        HttpMethod.DELETE,
                        httpEntity,
                        new ParameterizedTypeReference<>() {
                        });
    }

    private ResponseEntity<ApiResponse<List<Command>>> deleteCommands() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getEncodedAuthHeader());

        HttpEntity httpEntity = httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers);

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commands",
                        HttpMethod.DELETE,
                        httpEntity,
                        new ParameterizedTypeReference<>() {
                        });
    }

    private ResponseEntity<ApiResponse<CommandDescription>> postCommands(String command) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getEncodedAuthHeader());

        HttpEntity httpEntity = httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers);

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commands",
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<>() {
                        });
    }

    private ResponseEntity<ApiResponse<List<Command>>> getFinishedCommands() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getEncodedAuthHeader());

        HttpEntity httpEntity = httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers);

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commands/finished",
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<>() {
                        });
    }

    private ResponseEntity<ApiResponse<List<Command>>> getRunningCommands() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getEncodedAuthHeader());

        HttpEntity httpEntity = httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers);

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commands/running",
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<>() {
                        });
    }

    private ResponseEntity<ApiResponse<List<Command>>> getAllCommands() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getEncodedAuthHeader());

        HttpEntity httpEntity = httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers);

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commands",
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<>() {
                        });
    }

    private String getEncodedAuthHeader() {
        String auth = this.auth.getUser() + ":" + this.auth.getPassword();
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);

        return authHeader;
    }
}
