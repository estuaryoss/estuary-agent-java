package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.HeaderConstants;
import com.github.estuaryoss.agent.constants.QParamConstants;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.estuaryoss.agent.constants.DateTimeConstants.PATTERN;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class FileApiControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";
    private final String SOME_TEST_CONTENT = "some_test_content";

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
    public void whenCallingGetTheFileContentIsRetrievedOk() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "README.md");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file/read",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getDescription().toString()).contains("## Build status");
    }

    @Test
    public void whenCallingDownloadFileThenItIsRetrievedOk() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "README.md");

        ResponseEntity<String> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file/download",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                String.class);

        String body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.toString()).contains("## Build status");
    }

    @Test
    public void whenFilePathIsMissingThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file/read",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), HeaderConstants.FILE_PATH));
        assertThat(body.getDescription().toString()).contains(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), HeaderConstants.FILE_PATH));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenFilePathIsMissingWhenDownloadingFileThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file/download",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), HeaderConstants.FILE_PATH));
        assertThat(body.getDescription().toString()).contains(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), HeaderConstants.FILE_PATH));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenRequestingForFileUploadAndDownloadHistory_ThenApiReturnsSuccess() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SERVER_PREFIX + port + "/files");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(builder.build().encode().toUri(),
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, new HashMap<>()),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()), QParamConstants.FILE_PATH_Q_PARAM));
        assertThat(body.getDescription()).isInstanceOf(List.class);
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenFilePathIsWrongThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "whateverinvalid");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file/read",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.GET_FILE_FAILURE.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.getCode())));
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenFilePathIsWrongWhenDownloadingFileThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "whateverInvalidPath");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file/download",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.GET_FILE_FAILURE.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.getCode())));
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingFileAndFilePathIsWrongThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "whateverinvalid/a/imlazytoday/lazy.txt");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.PUT,
                                httpRequestUtils.getRequestEntityContentTypeAppJson("doesnotmatter", headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.UPLOAD_FILE_FAILURE.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.UPLOAD_FILE_FAILURE.getCode())));
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingFileAndFilePathIsCorrectThenApiReturnsSuccess() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "config.properties");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.PUT,
                                httpRequestUtils.getRequestEntityContentTypeAppJson("{\"ip\": \"localhost\"}", headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getDescription()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getPath()).isEqualTo("/file?");
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingFileAndBodyIsEmptyThenApiReturnsSuccess() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "myEmptyFile.txt");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.POST,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getDescription()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getPath()).isEqualTo("/file?");
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingMultipleFiles_ThenUploadIsSuccessful() throws IOException {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("files", getTestFile());
        requestBody.add("files", getTestFile());
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>() {{
            add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
            add("Accept", MediaType.APPLICATION_JSON_VALUE);
        }};

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/files",
                                HttpMethod.POST,
                                requestEntity,
                                ApiResponse.class);

        ApiResponse responseBody = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(responseBody.getMessage()).isEqualTo(String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(responseBody.getDescription().toString()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(responseBody.getName()).isEqualTo(about.getAppName());
        assertThat(responseBody.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(responseBody.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingMultipleFilesInTmpFolder_ThenUploadIsSuccessful() throws IOException {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("files", getTestFile());
        requestBody.add("files", getTestFile());
        String folderPath = "uploads";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>() {{
            add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
            add("Accept", MediaType.APPLICATION_JSON_VALUE);
            add("Folder-Path", folderPath);
        }};
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/files",
                                HttpMethod.POST,
                                requestEntity,
                                ApiResponse.class,
                                folderPath);

        ApiResponse responseBody = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(responseBody.getMessage()).isEqualTo(String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(responseBody.getDescription().toString()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(responseBody.getName()).isEqualTo(about.getAppName());
        assertThat(responseBody.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(responseBody.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingMultipleFilesInInvalidFolderPath_ThenException() throws IOException {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("files", getTestFile());
        requestBody.add("files", getTestFile());
        String folderPath = "whateverInvalidFolderPath";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>() {{
            add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
            add("Accept", MediaType.APPLICATION_JSON_VALUE);
            add("Folder-Path", folderPath);
        }};
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/files",
                                HttpMethod.POST,
                                requestEntity,
                                ApiResponse.class);

        ApiResponse responseBody = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(responseBody.getCode()).isEqualTo(ApiResponseCode.UPLOAD_FILE_FAILURE_NAME.getCode());
        assertThat(responseBody.getDescription().toString()).contains("Exception");
        assertThat(responseBody.getName()).isEqualTo(about.getAppName());
        assertThat(responseBody.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(responseBody.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    private Object getTestFile() throws IOException {
        Path tempFile = Files.createTempFile("upload-file", ".txt");
        Files.write(tempFile, SOME_TEST_CONTENT.getBytes(StandardCharsets.UTF_8));
        return new FileSystemResource(tempFile.toFile());
    }
}
