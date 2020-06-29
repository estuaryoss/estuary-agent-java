package com.github.dinuta.estuary.api;

import com.github.dinuta.estuary.api.models.ApiResponseString;
import com.github.dinuta.estuary.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.constants.About;
import com.github.dinuta.estuary.constants.ApiResponseConstants;
import com.github.dinuta.estuary.constants.ApiResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static com.github.dinuta.estuary.api.constants.HeaderConstants.TOKEN;
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

    @Test
    public void whenCallingWithValidTokenThenInformationIsRetrivedOk() {
        Map<String, String> headers = new HashMap<>();
        headers.put(TOKEN, "null");

        ResponseEntity<ApiResponseString> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/about",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponseString.class);

        ApiResponseString body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS));
        assertThat(body.getDescription()).isEqualTo(About.getAppName());
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(body.getTime()).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenCallingWithInvalidTokenThenNotAuthorized() {
        Map<String, String> headers = new HashMap<>();
        headers.put(TOKEN, "whateverinvalid");

        ResponseEntity<ApiResponseString> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/about",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponseString.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

}
