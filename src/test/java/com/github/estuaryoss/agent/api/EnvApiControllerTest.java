package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.github.estuaryoss.agent.constants.DateTimeConstants.PATTERN;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnvApiControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private About about;

    @Autowired
    private Authentication auth;

    @Test
    public void whenGettingAllEnvVarsThenInformationIsRetrivedOk() {
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get("PATH")).isNotEqualTo("");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getPath()).isEqualTo("/env?");
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenGettingVirtualEnvVarsThenInformationIsRetrivedOk() {
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .getForEntity(SERVER_PREFIX + port + "/env/virtual", ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get("PATH")).isEqualTo(null); //none of the system vars are here
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getPath()).isEqualTo("/env/virtual?");
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenGettingSystemEnvVarsThenInformationIsRetrivedOk() {
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .getForEntity(SERVER_PREFIX + port + "/env/system", ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get("PATH")).isNotEqualTo(""); //PATH is present
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getPath()).isEqualTo("/env/system?");
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenGettingExistentEnvVarThenInformationIsRetrivedOk() {
        String envVar = "PATH";
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword()).getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription()).isNotEqualTo("");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenSettingExternalEnvVarsWithInvalidBodyWithRestAPIThenError() {
        String envVars = "{whatever_invalid_json}";
        ResponseEntity<ApiResponse> responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(envVars, new HashMap<>()),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SET_ENV_VAR_FAILURE.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SET_ENV_VAR_FAILURE.getCode()), envVars), envVars);
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenGettingNotExistentEnvVarThenValueIsNull() {
        String envVar = "whatever";
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword()).getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isEqualTo(null);
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "FOO1;BAR1",
                    "FOO2;BAR2"
            }
    )
    @Order(1)
    public void whenSettingExternalEnvVarsFromFileThenInformationIsRetrivedOk(String envInfo) {
        String envVar = envInfo.split(";")[0];
        String expectedValue = envInfo.split(";")[1];
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword()).getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription()).isEqualTo(expectedValue);
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    @Order(2)
    public void whenSettingExternalEnvVarsFromFileAndItsASystemOneThenItDoesntGetOverwritten() {
        String envVar = "JAVA_HOME";
        String notExpectedValue = "this_value_wont_be_injected_because_its_an_existing_system_env_var";
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword()).getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription()).isNotEqualTo(notExpectedValue);
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "FOO1;BARx;BARx",
                    "FOO3;BAR3;BAR3",
                    "FOO4;{{FOO1}}/{{FOO3}};BARx/BAR3"
            }
    )
    @Order(3)
    public void whenSettingExternalEnvVarsWithRestAPIThenInformationIsRetrivedOk(String envVars) {
        String envVarName = envVars.split(";")[0];
        String envVarValue = envVars.split(";")[1];
        String expectedEnvVarValue = envVars.split(";")[2];
        String envVarsJson = String.format("{\"%s\":\"%s\"}", envVarName, envVarValue);
        ResponseEntity<ApiResponse> responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(envVarsJson, new HashMap<>()),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
        assertThat(((Map) body.getDescription()).get(envVarName)).isEqualTo(expectedEnvVarValue);

        responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword()).getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);
        body = responseEntity.getBody();

        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get(envVarName)).isEqualTo(expectedEnvVarValue);
    }


    @ParameterizedTest
    @ValueSource(
            strings = {
                    "FOO1;BARx",
                    "FOO3;BAR3"
            }
    )
    @Order(4)
    public void whenSettingExternalEnvVarsWithRestAPIThenSystemEnvVarsAreNotOverwritten(String envVars) {
        String envVarName = envVars.split(";")[0];
        String expectedEnvVarValue = envVars.split(";")[1];
        String attemptedShellEnvVarValue = "must_be_immutable";

        String envVarsJson = String.format("{\"%s\":\"%s\", \"PATH\": \"%s\"}",
                envVarName, expectedEnvVarValue, attemptedShellEnvVarValue);
        ResponseEntity<ApiResponse> responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(envVarsJson, new HashMap<>()),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).size()).isEqualTo(1);
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword()).getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);
        body = responseEntity.getBody();

        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get(envVarName)).isEqualTo(expectedEnvVarValue);
        assertThat(((Map) body.getDescription()).get("SHELL")).isNotEqualTo(attemptedShellEnvVarValue);
    }

//    @Test
//    @Order(9)
//    public void whenSettingVirtualEnvVarsThenAHardLimitIsReached() {
//        final int VIRTUAL_ENV_VARS_LIMIT_SIZE = AppEnvironment.VIRTUAL_ENVIRONMENT_MAX_SIZE;
//
//        for (int i = 0; i < 2 * VIRTUAL_ENV_VARS_LIMIT_SIZE; i++) {
//            String envVarsJson = String.format("{\"%s\":\"%s\"}", i, i);
//            this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
//                    .exchange(SERVER_PREFIX + port + "/env",
//                            HttpMethod.POST,
//                            httpRequestUtils.getRequestEntityContentTypeAppJson(envVarsJson, new HashMap<>()),
//                            new ParameterizedTypeReference<>() {
//                            });
//        }
//
//        ResponseEntity<ApiResponse<Map<String, String>>> responseEntity =
//                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
//                        .exchange(SERVER_PREFIX + port + "/env",
//                                HttpMethod.GET,
//                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, new HashMap<>()),
//                                new ParameterizedTypeReference<>() {
//                                });
//
//        ApiResponse<Map<String, String>> body = responseEntity.getBody();
//
//        assertThat(body.getDescription()).isInstanceOf(Map.class);
//        assertThat((body.getDescription()).get(String.valueOf(VIRTUAL_ENV_VARS_LIMIT_SIZE))).isEqualTo(null);
//    }

    @Test
    @Order(10)
    public void whenSettingVirtualEnvVarsAndDeletingAfterwardsThenAllCustomEnvVarsAreDeleted() {
        String envVarName = "FOO1";
        String envVarJson = String.format("{\"%s\":\"BAR1\"}", envVarName);

        ResponseEntity responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(envVarJson, new HashMap<>()),
                        ApiResponse.class);
        ApiResponse body = (ApiResponse) responseEntity.getBody();
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).size()).isGreaterThan(0);

        //delete all virtual env
        responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.DELETE,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, new HashMap<>()),
                        ApiResponse.class);
        body = (ApiResponse) responseEntity.getBody();
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).size()).isEqualTo(0);

        //check again with GET if it was deleted
        responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword()).getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);
        body = (ApiResponse) responseEntity.getBody();

        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get(envVarName)).isEqualTo(null);
    }

    @Test
    public void whenDeletingEnvVar_ThenOk() {
        String envVarName = "FOO1";
        String envVarJson = String.format("{\"%s\":\"BAR1\"}", envVarName);

        ResponseEntity responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(envVarJson, new HashMap<>()),
                        ApiResponse.class);
        ApiResponse body = (ApiResponse) responseEntity.getBody();
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).size()).isGreaterThan(0);

        //delete FOO anv var
        responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/env/" + envVarName,
                        HttpMethod.DELETE,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, new HashMap<>()),
                        ApiResponse.class);
        body = (ApiResponse) responseEntity.getBody();
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).size()).isEqualTo(0);

        //check again with GET if it was deleted
        responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword()).getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);
        body = (ApiResponse) responseEntity.getBody();

        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get(envVarName)).isEqualTo(null);
    }
}
