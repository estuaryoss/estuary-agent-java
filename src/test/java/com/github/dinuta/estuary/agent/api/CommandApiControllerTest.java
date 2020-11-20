package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.dinuta.estuary.agent.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.exception.YamlConfigException;
import com.github.dinuta.estuary.agent.model.YamlConfig;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.ApiResponseCommandDescription;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.dinuta.estuary.agent.constants.DateTimeConstants.PATTERN;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class CommandApiControllerTest {
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
                    "ls -lrt;README.md",
                    "ls -lrt | grep README.md;README.md",
                    "echo 1 && echo 2;1\n2",
            }
    )
    public void whenSendingCorrectCommandsThenApiReturnsZeroExitCode(String commandInfo) {
        ResponseEntity<ApiResponseCommandDescription> responseEntity = getApiResponseCommandDescriptionResponseEntity(commandInfo.split(";")[0]);

        ApiResponseCommandDescription body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));

        this.assertSuccessCommandDescriptionFields(commandInfo, body.getDescription());

        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenSendingOneCommandAndItExceedsTheCommandTimeoutSetInTheServiceThenTimeout() {
        float sleep = 4f; // default is 3 secs
        float timeout = 3f;
        String command = "sleep " + sleep;
        ResponseEntity<ApiResponseCommandDescription> responseEntity =
                getApiResponseCommandDescriptionResponseEntity(command);

        ApiResponseCommandDescription body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(Math.round(body.getDescription().getDuration())).isEqualTo(Math.round(timeout));
        assertThat(body.getDescription().getDuration()).isInstanceOf(Float.class);

        assertThat(Math.round(body.getDescription().getCommands().get(command).getDuration())).isEqualTo(Math.round(timeout));
        assertThat(body.getDescription().getCommands().get(command).getDuration()).isInstanceOf(Float.class);
        assertThat(body.getDescription().getCommands().get(command).getDetails().getOut()).isEqualTo("");
        assertThat(body.getDescription().getCommands().get(command).getDetails().getCode()).isEqualTo(-1);
        assertThat(body.getDescription().getCommands().get(command).getDetails().getErr()).containsIgnoringCase("TimeoutException");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenSendingTwoCommandsAndTheExecTimeExceedsTimeoutSetInTheServiceThenTimeout() {
        float sleep1 = 1f;
        float sleep2 = 4f;
        float timeout = 3f;

        String command1 = "sleep " + sleep1;
        String command2 = "sleep " + sleep2;
        String command = command1 + "\n" + command2;
        ResponseEntity<ApiResponseCommandDescription> responseEntity =
                getApiResponseCommandDescriptionResponseEntity(command);

        ApiResponseCommandDescription body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(Math.round(body.getDescription().getDuration())).isEqualTo(Math.round(sleep1 + timeout));
        assertThat(body.getDescription().getDuration()).isInstanceOf(Float.class);

        assertThat(Math.round(body.getDescription().getCommands().get(command1).getDuration())).isEqualTo(Math.round(sleep1));
        assertThat(body.getDescription().getCommands().get(command1).getDuration()).isInstanceOf(Float.class);
        assertThat(body.getDescription().getCommands().get(command1).getDetails().getErr()).isEqualTo("");

        assertThat(Math.round(body.getDescription().getCommands().get(command2).getDuration())).isEqualTo(Math.round(timeout));
        assertThat(body.getDescription().getCommands().get(command2).getDuration()).isInstanceOf(Float.class);
        assertThat(body.getDescription().getCommands().get(command2).getDetails().getOut()).isEqualTo("");
        assertThat(body.getDescription().getCommands().get(command2).getDetails().getCode()).isEqualTo(-1);
        assertThat(body.getDescription().getCommands().get(command2).getDetails().getErr()).containsIgnoringCase("TimeoutException");

        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenSendingTwoCommandsThenApiReturnsSumOfTimeExecutionInSeconds() {
        float sleep1 = 1f;
        float sleep2 = 2f;
        String command1 = "sleep " + sleep1;
        String command2 = "sleep " + sleep2;
        String command = command1 + "\n" + command2;
        ResponseEntity<ApiResponseCommandDescription> responseEntity =
                getApiResponseCommandDescriptionResponseEntity(command);

        ApiResponseCommandDescription body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(Math.round(body.getDescription().getDuration())).isEqualTo(Math.round(sleep1 + sleep2));
        assertThat(body.getDescription().getDuration()).isInstanceOf(Float.class);

        assertThat(Math.round(body.getDescription().getCommands().get(command1).getDuration())).isEqualTo(Math.round(sleep1));
        assertThat(body.getDescription().getCommands().get(command1).getDuration()).isInstanceOf(Float.class);
        assertThat(Math.round(body.getDescription().getCommands().get(command2).getDuration())).isEqualTo(Math.round(sleep2));
        assertThat(body.getDescription().getCommands().get(command2).getDuration()).isInstanceOf(Float.class);
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenSendingTwoCommandsOneSuccessOneFailureThenApiReturnsTheCorrectDetailsForEachOne() {
        String command1 = "ls -lrt";
        String command2 = "whatever";
        String command = command1 + "\n" + command2;
        ResponseEntity<ApiResponseCommandDescription> responseEntity =
                getApiResponseCommandDescriptionResponseEntity(command);

        ApiResponseCommandDescription body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getDescription().getDuration()).isInstanceOf(Float.class);

        assertThat(body.getDescription().getCommands().get(command1).getDetails().getCode()).isEqualTo(0L);
        assertThat(body.getDescription().getCommands().get(command2).getDetails().getCode()).isNotEqualTo(0L);
        assertThat(body.getDescription().getCommands().get(command1).getDetails().getOut()).isNotEqualTo("");
        assertThat(body.getDescription().getCommands().get(command2).getDetails().getOut()).isEqualTo("");
        assertThat(body.getDescription().getCommands().get(command1).getDetails().getErr()).isEqualTo("");
        assertThat(body.getDescription().getCommands().get(command2).getDetails().getErr()).isNotEqualTo("");

        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenSendingTheCommandsConfigYamlApiReturnsTheCorrectDetailsForEachOne() throws IOException, YamlConfigException {
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        ObjectMapper objectMapperJson = new ObjectMapper();
        YamlConfig yamlConfig = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        List<String> list = new YamlConfigParser().getCommandsList(yamlConfig);

        ResponseEntity<ApiResponse> responseEntity =
                getApiResponseConfigDescriptorResponseEntity(yamlConfigString);

        ApiResponse body = responseEntity.getBody();
        CommandDescription commandDescription = objectMapperJson.readValue(
                new JSONObject((Map) ((Map) body.getDescription()).get("description")).toJSONString(),
                CommandDescription.class);
        YamlConfig yamlConfigResponse = objectMapperJson.readValue(
                new JSONObject((Map) ((Map) body.getDescription()).get("config")).toJSONString(),
                YamlConfig.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));

        assertThat(yamlConfigResponse.getEnv()).isEqualTo(yamlConfig.getEnv());
        assertThat(yamlConfigResponse.getBeforeScript()).isEqualTo(yamlConfig.getBeforeScript());
        assertThat(yamlConfigResponse.getScript()).isEqualTo(yamlConfig.getScript());
        assertThat(yamlConfigResponse.getAfterScript()).isEqualTo(yamlConfig.getAfterScript());
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

    @Test
    public void whenSendingTheCommandsConfigYamlWithoutScriptSectionThenException() throws IOException, YamlConfigException {
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        YamlConfig yamlConfig = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        yamlConfig.setScript(new ArrayList<>());

        ResponseEntity<ApiResponse> responseEntity =
                getApiResponseResponseEntity(objectMapper.writeValueAsString(yamlConfigString));

        ApiResponse body = responseEntity.getBody();
        String description = body.getDescription().toString();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.INVALID_YAML_CONFIG.getCode());

        assertThat(body.getMessage()).isEqualTo(String.format(ApiResponseMessage.getMessage(ApiResponseCode.INVALID_YAML_CONFIG.getCode())));
        assertThat(description).contains("Exception");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "whatever;not found",
                    "ls whateverivalid;No such file or directory",
                    "cat whenever;No such file or directory",
            }
    )
    public void whenSendingIncorrectCommandsThenApiReturnsNonZeroExitCode(String commandInfo) {
        ResponseEntity<ApiResponseCommandDescription> responseEntity = getApiResponseCommandDescriptionResponseEntity(commandInfo.split(";")[0]);

        ApiResponseCommandDescription body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));

        this.assertFailureCommandDescriptionFields(commandInfo, body.getDescription());

        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getPath()).isEqualTo("/command?");
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    private ResponseEntity<ApiResponseCommandDescription> getApiResponseCommandDescriptionResponseEntity(String command) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/command",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers),
                        ApiResponseCommandDescription.class);
    }

    private ResponseEntity<ApiResponse> getApiResponseConfigDescriptorResponseEntity(String yamlConfig) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commandyaml",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(yamlConfig, headers),
                        ApiResponse.class);
    }

    private ResponseEntity<ApiResponse> getApiResponseResponseEntity(String yamlConfig) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commandyaml",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(yamlConfig, headers),
                        ApiResponse.class);
    }

    private void assertSuccessCommandDescriptionFields(String commandInfo, CommandDescription body) {
        String command = commandInfo.split(";")[0];
        String expected = commandInfo.split(";")[1];

        assertCommonCommonDescriptionFields(command, body);

        assertThat(body.getCommands().get(commandInfo.split(";")[0]).getDetails().getCode()).isEqualTo(0);
        assertThat(body.getCommands().get(commandInfo.split(";")[0]).getDetails().getErr()).isEqualTo("");
        assertThat(body.getCommands().get(commandInfo.split(";")[0]).getDetails().getOut()).contains(expected);
    }

    private void assertFailureCommandDescriptionFields(String commandInfo, CommandDescription body) {
        String command = commandInfo.split(";")[0];
        String expected = commandInfo.split(";")[1];

        assertCommonCommonDescriptionFields(command, body);

        assertThat(body.getCommands().get(command).getDetails().getCode()).isNotEqualTo(0);
        assertThat(body.getCommands().get(command).getDetails().getErr()).contains(expected);
        assertThat(body.getCommands().get(command).getDetails().getOut()).isEqualTo("");
    }

    private void assertCommonCommonDescriptionFields(String command, CommandDescription body) {
        assertThat(LocalDateTime.parse(body.getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body.getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body.getFinished()).isEqualTo(true);
        assertThat(body.getStarted()).isEqualTo(false);
        assertThat(body.getDuration()).isGreaterThanOrEqualTo(0);
        assertThat(body.getPid()).isGreaterThan(0);
        assertThat(body.getId()).isEqualTo("none");

        assertThat(LocalDateTime.parse(body.getCommands().get(command).getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body.getCommands().get(command).getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body.getCommands().get(command).getDuration()).isGreaterThanOrEqualTo(0);
        assertThat(body.getCommands().get(command).getStatus()).isEqualTo("finished");

        assertThat(body.getCommands().get(command).getDetails().getPid()).isGreaterThanOrEqualTo(0);
        assertThat(body.getCommands().get(command).getDetails().getArgs()).contains(command);
    }
}
