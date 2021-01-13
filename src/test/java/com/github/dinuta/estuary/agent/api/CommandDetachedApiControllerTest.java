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
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import com.github.dinuta.estuary.agent.utils.YamlConfigParser;
import com.google.gson.Gson;
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
import org.springframework.core.ParameterizedTypeReference;
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
    public void whenSendingCommandsWithSpacesNothingIsBrokenAndThenApiReturnsZeroExitCode(String commandInfo) throws InterruptedException {
        String id = "myId1";
        String expected = commandInfo.split(";")[1];

        ResponseEntity<ApiResponse<String>> responseEntity = postApiResponseCommandDescriptionEntity(commandInfo.split(";")[0], id);
        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getDescription()).isEqualTo(id);
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        Thread.sleep(1000);
        ApiResponse<CommandDescription> body1 = getApiResponseCommandDescriptionEntity().getBody();

        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getCode()).isEqualTo(0);
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getErr()).isEqualTo("");
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getOut()).contains(expected);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "ls -lrt | grep README.md;README.md"
            }
    )
    public void whenAskingForExistingDetachedCommandIdThenItIsFound(String commandInfo) throws InterruptedException {
        String id = "myId10";
        String expected = commandInfo.split(";")[1];

        ResponseEntity<ApiResponse<String>> responseEntity = postApiResponseCommandDescriptionEntity(commandInfo.split(";")[0], id);
        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getDescription()).isEqualTo(id);
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        Thread.sleep(1000);
        ApiResponse<CommandDescription> body1 = getApiResponseCommandDescriptionEntityForId(id).getBody();

        assertThat(body1.getDescription().getId()).isEqualTo(id);
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getCode()).isEqualTo(0);
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getErr()).isEqualTo("");
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getOut()).contains(expected);
    }

    @Test
    public void whenSendingDetachedCommandAndThenStreamingOutputhenTheOutputIsIncremental() throws InterruptedException {
        String id = "myId101";
        String command = "echo 1 && sleep 1 && echo 2 && sleep 1 && echo 3 && sleep 1";

        ResponseEntity<ApiResponse<String>> responseEntity = postApiResponseCommandDescriptionEntity(command, id);
        ApiResponse<String> body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getDescription()).isEqualTo(id);
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        Thread.sleep(1000);
        ApiResponse<CommandDescription> body1 = getApiResponseCommandDescriptionEntityForId(id).getBody();
        Thread.sleep(2000);
        ApiResponse<CommandDescription> body2 = getApiResponseCommandDescriptionEntityForId(id).getBody();

        assertThat(body1.getDescription().getId()).isEqualTo(id);
        assertThat(body2.getDescription().getId()).isEqualTo(id);
        assertThat(body2.getDescription().getCommands().get(command).getDetails().getOut().length())
                .isGreaterThan(body1.getDescription().getCommands().get(command).getDetails().getOut().length());
        assertThat(body1.getDescription().getCommands().get(command).getDetails().getErr()).isEqualTo("");
        assertThat(body2.getDescription().getCommands().get(command).getDetails().getErr()).isEqualTo("");
    }

    @Test
    public void whenAskingForNonExistentDetachedCommandIdThenItIsNotFound() throws InterruptedException {
        String id = "myId11";
        String command = "ls -lart";

        ResponseEntity<ApiResponse<String>> responseEntity = postApiResponseCommandDescriptionEntity(command, id);
        ApiResponse<String> body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getDescription()).isEqualTo(id);
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        Thread.sleep(1000);
        ApiResponse body1 = getApiResponseEntityForId(id + "invalid").getBody();

        assertThat(body1.getDescription().toString()).contains("Exception");
        assertThat(body1.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_DETACHED_INFO_FAILURE.getCode()));
        assertThat(body1.getCode()).isEqualTo(ApiResponseCode.GET_COMMAND_DETACHED_INFO_FAILURE.getCode());
    }

    @Test
    public void whenSendingTwoCommandsThenApiReturnsSumOfTimeExecutionInSeconds() {
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

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getDescription()).isEqualTo(testId);


        await().atMost(sleep1 + 2, SECONDS).until(isCommandFinished(command1));
        ApiResponse<CommandDescription> body1 =
                getApiResponseCommandDescriptionEntity().getBody();

        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().isStarted()).isEqualTo(true);
        assertThat(body1.getDescription().isFinished()).isEqualTo(false);
        assertThat(body1.getDescription().getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(body1.getDescription().getCommands().get(command2).getStatus()).isEqualTo("in progress");

        await().atMost(sleep2 + 2, SECONDS).until(isCommandFinished(command2));
        body1 = getApiResponseCommandDescriptionEntity().getBody();

        assertThat(Math.round(body1.getDescription().getDuration())).isEqualTo(Math.round(sleep1 + sleep2));
        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().isStarted()).isEqualTo(false);
        assertThat(body1.getDescription().isFinished()).isEqualTo(true);
        assertThat(body1.getDescription().getId()).isEqualTo(testId);
        assertThat(body1.getDescription().getPid()).isGreaterThanOrEqualTo(0);
        assertThat(Math.round(body1.getDescription().getCommands().get(command1).getDuration())).isEqualTo(Math.round(sleep1));
        assertThat(body1.getDescription().getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(Math.round(body1.getDescription().getCommands().get(command2).getDuration())).isEqualTo(Math.round(sleep2));
        assertThat(body1.getDescription().getCommands().get(command2).getStatus()).isEqualTo("finished");
        assertThat(body1.getName()).isEqualTo(About.getAppName());
        assertThat(body1.getPath()).isEqualTo("/commanddetached?");
        assertThat(body1.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body1.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenDeletingANonExistentIdThenException() {
        String id = "this_is_does_not_exist_";
        ResponseEntity<ApiResponse> response = deleteApiResponseEntityForId(id);

        ApiResponse body = response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getDescription().toString()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
    }

    @Test
    public void whenDeletingAnExistentIdThenProcessesAreDeleted() throws InterruptedException {
        String testId = "myInventedId";
        String command = "sleep 400 && echo 400";
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + testId,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityJsonContentTypeAppText(command, headers),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getDescription()).isEqualTo(testId);
        Thread.sleep(1000);
        ApiResponse<CommandDescription> body1 =
                getApiResponseCommandDescriptionEntity().getBody();
        int pid = (int) body1.getDescription().getPid();

        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().isStarted()).isEqualTo(true);
        assertThat(body1.getDescription().isFinished()).isEqualTo(false);
        assertThat(body1.getDescription().getId()).isEqualTo(testId);
        assertThat(new Gson().toJson(body1.getDescription().getProcesses())).contains(String.valueOf(pid));

        ResponseEntity<ApiResponse> response = deleteApiResponseEntityForId(testId);

        body = response.getBody();
        System.out.println("=> Received body: " + body.toString());
        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));

        Thread.sleep(1000);
        body1 = getApiResponseCommandDescriptionEntity().getBody();

        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().getId()).isEqualTo(testId);
        assertThat(new Gson().toJson(body1.getDescription().getProcesses())).doesNotContain(String.valueOf(pid));
    }

    @Test
    public void whenSendingTheCommandsConfigYamlApiReturnsTheCorrectDetailsForEachOne() throws IOException, YamlConfigException {
        String testId = "testIdYaml";
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        ObjectMapper objectMapperJson = new ObjectMapper();
        YamlConfig yamlConfig = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        List<String> list = new YamlConfigParser().getCommandsList(yamlConfig);

        ResponseEntity<ApiResponse> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetachedyaml/" + testId,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityJsonContentTypeAppText(yamlConfigString, new HashMap<>()),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();
        String commandDescription = ((Map) body.getDescription()).get("description").toString();
        YamlConfig yamlConfigResponse = objectMapperJson.readValue(
                new JSONObject((Map) ((Map) body.getDescription()).get("config")).toJSONString(),
                YamlConfig.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(commandDescription).isEqualTo(testId);
        assertThat(yamlConfigResponse.getEnv()).isEqualTo(yamlConfig.getEnv());
        assertThat(yamlConfigResponse.getBeforeScript()).isEqualTo(yamlConfig.getBeforeScript());
        assertThat(yamlConfigResponse.getScript()).isEqualTo(yamlConfig.getScript());
        assertThat(yamlConfigResponse.getAfterScript()).isEqualTo(yamlConfig.getAfterScript());

        await().atMost(2, SECONDS).until(isCommandFinished(list.get(0)));
        ResponseEntity<ApiResponse> responseEntityCmdDescription =
                getApiResponseCommandDescriptionEntity();
        ApiResponse<CommandDescription> bodyCmdDescription = responseEntityCmdDescription.getBody();

        assertThat(responseEntityCmdDescription.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(bodyCmdDescription.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(bodyCmdDescription.getMessage()).isEqualTo(String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(bodyCmdDescription.getDescription().getCommands().get(list.get(0)).getDetails().getCode()).isEqualTo(0L);
        assertThat(bodyCmdDescription.getDescription().getCommands().get(list.get(1)).getDetails().getCode()).isEqualTo(0L);
        assertThat(bodyCmdDescription.getDescription().getCommands().get(list.get(2)).getDetails().getCode()).isEqualTo(0L);
        assertThat(bodyCmdDescription.getDescription().getCommands().get(list.get(0)).getDetails().getOut()).contains("before_script");
        assertThat(bodyCmdDescription.getDescription().getCommands().get(list.get(1)).getDetails().getOut()).contains("script");
        assertThat(bodyCmdDescription.getDescription().getCommands().get(list.get(2)).getDetails().getOut()).contains("after_script");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    public Callable<Boolean> isCommandFinished(String command) {
        return () -> {
            ResponseEntity<ApiResponse> responseEntity = getApiResponseCommandDescriptionEntity();
            ApiResponse<CommandDescription> body = responseEntity.getBody();

            try {
                if (body.getDescription().getCommands().get(command) == null)
                    return Boolean.FALSE;

                return Boolean.valueOf(body.getDescription().getCommands().get(command).getStatus().equals("finished"));
            } catch (Exception e) {
                return false;
            }
        };
    }


    private ResponseEntity<ApiResponse> getApiResponseCommandDescriptionEntity() {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached",
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        ApiResponse.class);
    }

    private ResponseEntity<ApiResponse<CommandDescription>> getApiResponseCommandDescriptionEntityForId(String id) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        new ParameterizedTypeReference<ApiResponse<CommandDescription>>() {
                        });
    }

    private ResponseEntity<ApiResponse> getApiResponseEntityForId(String id) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        ApiResponse.class);
    }

    private ResponseEntity<ApiResponse> deleteApiResponseEntityForId(String id) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.DELETE,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        ApiResponse.class);
    }

    private ResponseEntity<ApiResponse<String>> postApiResponseCommandDescriptionEntity(String command, String id) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers),
                        new ParameterizedTypeReference<ApiResponse<String>>() {
                        });
    }
}
