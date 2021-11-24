package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import com.github.estuaryoss.agent.component.Authentication;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.repository.FileTransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class FileApiControllerRepoTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Authentication auth;

    @Autowired
    private FileTransferRepository fileTransferRepository;

    @BeforeEach
    public void cleanRepo() {
        fileTransferRepository.deleteAll();
    }

    @Test
    public void whenSendingSomeContentToBeUploadedAsFile_TheRepoContainsTheEntry() {
        String content = "whatever content";

        putFileContent(content);

        assertThat(fileTransferRepository.findAll().size()).isEqualTo(1);
    }

    private ResponseEntity<ApiResponse> putFileContent(String content) {
        String filePath = "/tmp/RepoContent.txt";
        Map<String, String> headers = new HashMap<>();
        headers.put("File-Path", filePath);


        return this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/file",
                        HttpMethod.PUT,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(content, headers),
                        ApiResponse.class);
    }
}
