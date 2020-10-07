package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.dinuta.estuary.agent.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseConstants;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.exception.YamlConfigException;
import com.github.dinuta.estuary.agent.model.ConfigDescriptor;
import com.github.dinuta.estuary.agent.model.YamlConfig;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import com.github.dinuta.estuary.agent.utils.YamlConfigParser;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.github.dinuta.estuary.agent.constants.DateTimeConstants.PATTERN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class CommandDetachedApiControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";
    private final static String YAML_CONFIG = "config.yaml";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "ls -lrt | grep README.md;README.md"
            }
    )
    public void whenSendingCommandsWithSpacesNothingIsBrokenAndThenApiReturnsZeroExitCode(String commandInfo) throws InterruptedException, JsonProcessingException {
        String id = "myId1";
        String expected = commandInfo.split(";")[1];

        ResponseEntity<ApiResponse> responseEntity = postApiResponseCommandDescriptionResponseEntity(commandInfo.split(";")[0], id);
        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getDescription()).isEqualTo(id);
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        Thread.sleep(1000);
        ApiResponse body1 = getApiResponseCommandDescriptionResponseEntity().getBody();
        CommandDescription commandDescription = new ObjectMapper().readValue(
                new JSONObject((Map) body1.getDescription()).toJSONString(), CommandDescription.class);

        assertThat(commandDescription.getCommands().get(commandInfo.split(";")[0]).getDetails().getCode()).isEqualTo(0);
        assertThat(commandDescription.getCommands().get(commandInfo.split(";")[0]).getDetails().getErr()).isEqualTo("");
        assertThat(commandDescription.getCommands().get(commandInfo.split(";")[0]).getDetails().getOut()).contains(expected);
    }

    @Test
    public void whenSendingTwoCommandsThenApiReturnsSumOfTimeExecutionInSeconds() throws JsonProcessingException {
        String testId = "myId2";
        int sleep1 = 2;
        int sleep2 = 3;
        String command1 = "sleep " + sleep1;
//        String command1 = "ping -n " + sleep1 + " 127.0.0.1";
        String command2 = "sleep " + sleep2;
//        String command2 = "ping -n " + sleep2 + " 127.0.0.1";
        String command = command1 + "\n" + command2;
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + testId,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityJsonContentTypeAppText(command, headers),
                        ApiResponse.class);


        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)));
        assertThat(body.getDescription()).isEqualTo(testId);


        await().atMost(sleep1 + 1, SECONDS).until(isCommandFinished(command1));
        ApiResponse body1 =
                getApiResponseCommandDescriptionResponseEntity().getBody();
        CommandDescription commandDescription = new ObjectMapper().readValue(
                new JSONObject((Map) body1.getDescription()).toJSONString(), CommandDescription.class);


        assertThat(LocalDateTime.parse(commandDescription.getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(commandDescription.getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(commandDescription.getStarted()).isEqualTo(true);
        assertThat(commandDescription.getFinished()).isEqualTo(false);
        assertThat(commandDescription.getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(commandDescription.getCommands().get(command2).getStatus()).isEqualTo("in progress");

        await().atMost(sleep2 + 1, SECONDS).until(isCommandFinished(command2));
        body1 = getApiResponseCommandDescriptionResponseEntity().getBody();

        assertThat(Math.round(commandDescription.getDuration())).isEqualTo(Math.round(sleep1 + sleep2));
        assertThat(LocalDateTime.parse(commandDescription.getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(commandDescription.getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(commandDescription.getStarted()).isEqualTo(false);
        assertThat(commandDescription.getFinished()).isEqualTo(true);
        assertThat(commandDescription.getId()).isEqualTo(testId);
        assertThat(commandDescription.getPid()).isGreaterThanOrEqualTo(0);
        assertThat(Math.round(commandDescription.getCommands().get(command1).getDuration())).isEqualTo(Math.round(sleep1));
        assertThat(commandDescription.getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(Math.round(commandDescription.getCommands().get(command2).getDuration())).isEqualTo(Math.round(sleep2));
        assertThat(commandDescription.getCommands().get(command2).getStatus()).isEqualTo("finished");
        assertThat(body1.getName()).isEqualTo(About.getAppName());
        assertThat(body1.getPath()).isEqualTo("/commanddetached?");
        assertThat(body1.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body1.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenSendingTheCommandsConfigYamlApiReturnsTheCorrectDetailsForEachOne() throws IOException, YamlConfigException {
        String testId = "testIdYaml";
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        YamlConfig yamlConfig = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        List<String> list = new YamlConfigParser().getCommandsList(yamlConfig);

        ResponseEntity<ApiResponse> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetachedyaml/" + testId,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityJsonContentTypeAppText(yamlConfigString, new HashMap<>()),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();
        ConfigDescriptor configDescriptor = new ObjectMapper().readValue(
                new JSONObject((Map) body.getDescription()).toJSONString(), ConfigDescriptor.class);
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(configDescriptor.getDescription().toString()).isEqualTo(testId);
        assertThat(configDescriptor.getYamlConfig().getEnv()).isEqualTo(yamlConfig.getEnv());
        assertThat(configDescriptor.getYamlConfig().getBeforeScript()).isEqualTo(yamlConfig.getBeforeScript());
        assertThat(configDescriptor.getYamlConfig().getScript()).isEqualTo(yamlConfig.getScript());
        assertThat(configDescriptor.getYamlConfig().getAfterScript()).isEqualTo(yamlConfig.getAfterScript());

        await().atMost(2, SECONDS).until(isCommandFinished(list.get(0)));
        ResponseEntity<ApiResponse> responseEntityCmdDescription =
                getApiResponseCommandDescriptionResponseEntity();
        ApiResponse bodyCmdDescription = responseEntityCmdDescription.getBody();
        CommandDescription commandDescription = new ObjectMapper().readValue(
                new JSONObject((Map) bodyCmdDescription).toJSONString(), CommandDescription.class);

        assertThat(responseEntityCmdDescription.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(bodyCmdDescription.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(bodyCmdDescription.getMessage()).isEqualTo(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)));
        assertThat(commandDescription.getCommands().get(list.get(0)).getDetails().getCode()).isEqualTo(0L);
        assertThat(commandDescription.getCommands().get(list.get(1)).getDetails().getCode()).isEqualTo(0L);
        assertThat(commandDescription.getCommands().get(list.get(2)).getDetails().getCode()).isEqualTo(0L);
        assertThat(commandDescription.getCommands().get(list.get(0)).getDetails().getOut()).contains("before_script");
        assertThat(commandDescription.getCommands().get(list.get(1)).getDetails().getOut()).contains("script");
        assertThat(commandDescription.getCommands().get(list.get(2)).getDetails().getOut()).contains("after_script");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    public Callable<Boolean> isCommandFinished(String command) {
        return () -> {
            ResponseEntity<ApiResponse> responseEntity = getApiResponseCommandDescriptionResponseEntity();
            ApiResponse body = responseEntity.getBody();
            CommandDescription commandDescription = new ObjectMapper().readValue(
                    new JSONObject((Map) body.getDescription()).toJSONString(), CommandDescription.class);


            if (commandDescription.getCommands().get(command) == null)
                return Boolean.FALSE;

            return Boolean.valueOf(commandDescription.getCommands().get(command).getDuration() > 0F);
        };
    }


    private ResponseEntity<ApiResponse> getApiResponseCommandDescriptionResponseEntity() {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached",
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        ApiResponse.class);
    }

    private ResponseEntity<ApiResponse> postApiResponseCommandDescriptionResponseEntity(String command, String id) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers),
                        ApiResponse.class);
    }
}
