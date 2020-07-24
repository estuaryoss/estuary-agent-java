package com.github.dinuta.estuary.testrunner.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.testrunner.constants.About;
import com.github.dinuta.estuary.testrunner.constants.ApiResponseConstants;
import com.github.dinuta.estuary.testrunner.constants.ApiResponseMessage;
import com.github.dinuta.estuary.testrunner.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

@Api(tags = {"estuary-testrunner"})
@Controller
public class PingApiController implements PingApi {

    private static final Logger log = LoggerFactory.getLogger(PingApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    public PingApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> pingGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<ApiResponse>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .description("pong")
                .name(About.getAppName())
                .version(About.getVersion())
                .time(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))), HttpStatus.OK);
    }

}
