package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
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
public class AuthTest {
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
    public void whenCallingWithValidCredentials_ThenInformationIsRetrivedOk() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/about",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenCallingWithInvalidPassword_ThenNotAuthorized() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), "whatever_invalid")
                        .exchange(SERVER_PREFIX + port + "/about",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void whenCallingWithInvalidUser_ThenNotAuthorized() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth("invalid", auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/about",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

}
