package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.component.Authentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class HomeControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Authentication auth;

    @Test
    public void whenCallingRootUrlThenInformationIsFound() {
        ResponseEntity<String> responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .getForEntity(SERVER_PREFIX + port + "/",
                        String.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.FOUND.value());
    }

    @Test
    public void whenCallingSwaggerUiThenInformationIsRetrivedOk() {
        ResponseEntity<String> responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .getForEntity(SERVER_PREFIX + port + "/swagger-ui/index.html", String.class);

        String body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body).isInstanceOf(String.class);
        assertThat(body).contains("<title>Swagger UI</title>");
    }

    @Test
    public void whenCallingApiDocsThenInformationIsRetrivedOk() {
        ResponseEntity<Map> responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .getForEntity(SERVER_PREFIX + port + "/v3/api-docs", Map.class);
        Map body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.get("openapi")).isEqualTo("3.0.1");
        assertThat(((Map) body.get("paths")).size()).isGreaterThanOrEqualTo(18);
    }
}
