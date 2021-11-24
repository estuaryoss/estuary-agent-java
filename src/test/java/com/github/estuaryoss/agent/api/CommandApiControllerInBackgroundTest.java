package com.github.estuaryoss.agent.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.entity.FinishedCommand;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.model.api.CommandDescription;
import com.github.estuaryoss.agent.repository.ActiveCommandRepository;
import com.github.estuaryoss.agent.repository.FinishedCommandRepository;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
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
    private ActiveCommandRepository activeCommandRepository;

    @Autowired
    private FinishedCommandRepository finishedCommandRepository;

    @BeforeEach
    public void cleanRepos() {
        finishedCommandRepository.deleteAll();
        activeCommandRepository.deleteAll();
    }

    @Test
    public void whenSendingBackgroundCommandThenTheUserMustPollActiveAndFinishedEndpointsForDetails() throws JsonProcessingException {
        String stdOut = "1";
        String command = "echo " + stdOut;

        postCommands(command);
        ResponseEntity<ApiResponse<List<FinishedCommand>>> requestEntity = getFinishedCommands();
        ApiResponse<List<FinishedCommand>> body = requestEntity.getBody();
        List<FinishedCommand> finishedCommandList = body.getDescription();

        assertThat(finishedCommandRepository.findAll().size()).isEqualTo(1);
        assertThat(finishedCommandList.size()).isEqualTo(1); //reads from finishedCommandRepo
        assertThat(objectMapper.writeValueAsString(finishedCommandList))
                .isEqualTo(objectMapper.writeValueAsString(finishedCommandRepository.findAll()));
        assertThat(finishedCommandList.get(0).getCommand()).isEqualTo(command);
        assertThat(finishedCommandList.get(0).getCode()).isEqualTo(0);
        assertThat(finishedCommandList.get(0).getOut().trim()).isEqualTo(stdOut);
        assertThat(finishedCommandList.get(0).getErr()).isEqualTo("");
        assertThat(finishedCommandList.get(0).getPid()).isGreaterThan(0);
        assertThat(finishedCommandList.get(0).getDuration()).isGreaterThan(0);
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

    private ResponseEntity<ApiResponse<List<FinishedCommand>>> getFinishedCommands() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getEncodedAuthHeader());

        HttpEntity httpEntity = httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers);

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commandsfinished",
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
