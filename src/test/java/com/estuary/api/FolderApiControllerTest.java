package com.estuary.api;

import com.estuary.api.models.ApiResponseString;
import com.estuary.api.utils.HttpRequestUtils;
import com.estuary.constants.About;
import com.estuary.constants.ApiResponseConstants;
import com.estuary.constants.ApiResponseMessage;
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

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.estuary.api.constants.HeaderConstants.FOLDER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class FolderApiControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenCallingGetThenTheFolderIsRetrivedOkInZipFormat() {
        Map<String, String> headers = new HashMap<>();
        headers.put(FOLDER_PATH, "src");

        ResponseEntity<String> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/folder",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                String.class);

        String body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body).isNotEmpty();
        //check also it appeared on disk
        assertThat(new File("results.zip").exists()).isTrue();
    }

    @Test
    public void whenFolderPathIsMissingThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponseString> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/folder",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponseString.class);

        ApiResponseString body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED), FOLDER_PATH));
        assertThat(body.getDescription()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED), FOLDER_PATH));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(body.getTime()).isBefore(LocalDateTime.now());

    }

    @Test
    public void whenFolderPathIsWrongThenApiReturnsError() {
        String folderName = "whateverinvalid";
        Map<String, String> headers = new HashMap<>();
        headers.put(FOLDER_PATH, folderName);

        ResponseEntity<ApiResponseString> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/folder",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponseString.class);

        ApiResponseString body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.FOLDER_ZIP_FAILURE);
        assertThat(body.getMessage()).isEqualTo(
                String.format(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.FOLDER_ZIP_FAILURE), folderName)));
        assertThat(body.getDescription()).contains("Exception");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(body.getTime()).isBefore(LocalDateTime.now());
    }
}
