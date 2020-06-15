package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.constants.About;
import io.swagger.constants.ApiResponseConstants;
import io.swagger.constants.ApiResponseDescription;
import io.swagger.model.ApiResponseSuccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.threeten.bp.OffsetDateTime;

import javax.servlet.http.HttpServletRequest;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

@Controller
public class EnvApiController implements EnvApi {

    private static final Logger log = LoggerFactory.getLogger(EnvApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public EnvApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponseSuccess> envEnvNameGet(@ApiParam(value = "The name of the env var to get value from", required = true) @PathVariable("env_name") String envName, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return new ResponseEntity<ApiResponseSuccess>(new ApiResponseSuccess()
                    .code(ApiResponseConstants.SUCCESS)
                    .description(ApiResponseDescription.getDescription(ApiResponseConstants.SUCCESS))
                    .message(System.getenv(envName))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .time(OffsetDateTime.now()), HttpStatus.OK);
        }

        return new ResponseEntity<ApiResponseSuccess>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ApiResponseSuccess> envGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return new ResponseEntity<ApiResponseSuccess>(new ApiResponseSuccess()
                    .code(ApiResponseConstants.SUCCESS)
                    .description(ApiResponseDescription.getDescription(ApiResponseConstants.SUCCESS))
                    .message(System.getenv())
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .time(OffsetDateTime.now()), HttpStatus.OK);
        }

        return new ResponseEntity<ApiResponseSuccess>(HttpStatus.NOT_IMPLEMENTED);
    }
}
