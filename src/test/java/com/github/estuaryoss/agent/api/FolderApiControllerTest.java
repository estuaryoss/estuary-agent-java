package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.HeaderConstants;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.repository.FileTransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.github.estuaryoss.agent.constants.DateTimeConstants.PATTERN;
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

    @Autowired
    private About about;

    @Autowired
    private Authentication auth;

    @Autowired
    private FileTransferRepository fileTransferRepository;

    @BeforeEach
    public void cleanRepo() {
        fileTransferRepository.deleteAll();
    }

    @Test
    public void whenCallingGetThenTheFolderIsRetrievedOkInZipFormat() throws IOException {
        Map<String, String> headers = new HashMap<>();
        String folderNameToBeRetrieved = "docs";
        headers.put(HeaderConstants.FOLDER_PATH, folderNameToBeRetrieved);

        ResponseEntity<Resource> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/folder",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                Resource.class);

        Resource body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.contentLength()).isGreaterThan(0L);
        //check also has the required name
        assertThat(body.getFilename()).isEqualTo(folderNameToBeRetrieved + ".zip");
        //audit transfer in DB
        assertThat(fileTransferRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void whenFolderPathIsMissingThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/folder",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), HeaderConstants.FOLDER_PATH));
        assertThat(body.getDescription().toString()).contains(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), HeaderConstants.FOLDER_PATH));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

    }

    @Test
    public void whenFolderPathIsWrongThenApiReturnsError() {
        String folderName = "whateverinvalid";
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FOLDER_PATH, folderName);
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/folder",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(String.format(ApiResponseMessage.getMessage(ApiResponseCode.FOLDER_ZIP_FAILURE.getCode()), folderName)));
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getPath()).isEqualTo("/folder?");
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }
}
