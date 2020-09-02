package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseConstants;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static com.github.dinuta.estuary.agent.constants.DateTimeConstants.PATTERN;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class EnvApiControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenCallingGetThenInformationIsRetrivedOk() {
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get("PATH")).isNotEqualTo("");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getPath()).isEqualTo("/env?");
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenGettingExistentEnvVarThenInformationIsRetrivedOk() {
        String envVar = "PATH";
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS));
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription()).isNotEqualTo("");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "FOO1;BAR1",
                    "FOO2;BAR2"
            }
    )
    public void whenGettingExternalEnvVarsFromFileThenInformationIsRetrivedOk(String envInfo) {
        String envVar = envInfo.split(";")[0];
        String expectedValue = envInfo.split(";")[1];
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS));
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription()).isEqualTo(expectedValue);
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenGettingNotExistentEnvVarThenValueIsNull() {
        String envVar = "whatever";
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS));
        assertThat(body.getDescription()).isEqualTo(null);
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }
}
