package com.github.dinuta.estuary.testrunner.api;

import com.github.dinuta.estuary.testrunner.api.models.ApiResponseCommandDescription;
import com.github.dinuta.estuary.testrunner.api.models.ApiResponseString;
import com.github.dinuta.estuary.testrunner.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.testrunner.constants.About;
import com.github.dinuta.estuary.testrunner.constants.ApiResponseConstants;
import com.github.dinuta.estuary.testrunner.constants.ApiResponseMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class TestApiControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenSendingTwoCommandsThenApiReturnsSumOfTimeExecutionInSeconds() throws InterruptedException {
        String testId = "myTestId";
        int sleep1 = 2;
        int sleep2 = 3;
        String command1 = "sleep " + sleep1;
//        String command1 = "ping -n " + sleep1 + " 127.0.0.1";
        String command2 = "sleep " + sleep2;
//        String command2 = "ping -n " + sleep2 + " 127.0.0.1";
        String command = command1 + "\n" + command2;
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponseString> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/test/" + testId,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityJsonContentTypeAppText(command, headers),
                        ApiResponseString.class);


        ApiResponseString body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)));
        assertThat(body.getDescription()).isEqualTo(testId);


        await().atMost(sleep1 + 1, SECONDS).until(isCommandFinished(command1));
        ApiResponseCommandDescription body1 =
                getApiResponseCommandDescriptionResponseEntity().getBody();

        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().getStarted()).isEqualTo(true);
        assertThat(body1.getDescription().getFinished()).isEqualTo(false);
        assertThat(body1.getDescription().getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(body1.getDescription().getCommands().get(command2).getStatus()).isEqualTo("in progress");

        await().atMost(sleep2 + 1, SECONDS).until(isCommandFinished(command2));
        body1 = getApiResponseCommandDescriptionResponseEntity().getBody();

        assertThat(Math.round(body1.getDescription().getDuration())).isEqualTo(Math.round(sleep1 + sleep2));
        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().getStarted()).isEqualTo(false);
        assertThat(body1.getDescription().getFinished()).isEqualTo(true);
        assertThat(body1.getDescription().getId()).isEqualTo(testId);
        assertThat(body1.getDescription().getPid()).isGreaterThanOrEqualTo(0);
        assertThat(Math.round(body1.getDescription().getCommands().get(command1).getDuration())).isEqualTo(Math.round(sleep1));
        assertThat(body1.getDescription().getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(Math.round(body1.getDescription().getCommands().get(command2).getDuration())).isEqualTo(Math.round(sleep2));
        assertThat(body1.getDescription().getCommands().get(command2).getStatus()).isEqualTo("finished");
        assertThat(body1.getName()).isEqualTo(About.getAppName());
        assertThat(body1.getVersion()).isEqualTo(About.getVersion());
        assertThat(body1.getTime()).isBefore(LocalDateTime.now());
    }

    public Callable<Boolean> isCommandFinished(String command) {
        return () -> {
            ResponseEntity<ApiResponseCommandDescription> responseEntity = getApiResponseCommandDescriptionResponseEntity();
            ApiResponseCommandDescription body = responseEntity.getBody();

            if (body.getDescription().getCommands().get(command) == null)
                return Boolean.FALSE;

            return Boolean.valueOf(body.getDescription().getCommands().get(command).getDuration() > 0F);
        };
    }


    private ResponseEntity<ApiResponseCommandDescription> getApiResponseCommandDescriptionResponseEntity() {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/test",
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        ApiResponseCommandDescription.class);
    }
}
