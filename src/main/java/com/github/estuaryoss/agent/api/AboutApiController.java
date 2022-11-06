package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.component.ClientRequest;
import com.github.estuaryoss.agent.constants.ApiResponseCode;
import com.github.estuaryoss.agent.constants.ApiResponseMessage;
import com.github.estuaryoss.agent.constants.DateTimeConstants;
import com.github.estuaryoss.agent.model.api.ApiResponse;
import com.github.estuaryoss.agent.utils.SystemInformation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Tag(name = "estuary-agent", description = "estuary-agent swagger API")
@RestController
@Slf4j
public class AboutApiController implements AboutApi {
    private final HttpServletRequest request;
    private final ClientRequest clientRequest;
    private final About about;

    @Autowired
    public AboutApiController(ClientRequest clientRequest, About about, HttpServletRequest request) {
        this.clientRequest = clientRequest;
        this.about = about;
        this.request = request;
    }

    public ResponseEntity aboutGet() {
        String accept = request.getHeader("Accept");

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(SystemInformation.getSystemInfo())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

}
