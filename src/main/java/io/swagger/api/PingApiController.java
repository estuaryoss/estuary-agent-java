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
import org.springframework.web.bind.annotation.RequestHeader;
import org.threeten.bp.OffsetDateTime;

import javax.servlet.http.HttpServletRequest;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

@Controller
public class PingApiController implements PingApi {

    private static final Logger log = LoggerFactory.getLogger(PingApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public PingApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponseSuccess> pingGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return new ResponseEntity<ApiResponseSuccess>(new ApiResponseSuccess()
                    .code(ApiResponseConstants.SUCCESS)
                    .description(ApiResponseDescription.getDescription(ApiResponseConstants.SUCCESS))
                    .message("pong")
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .time(OffsetDateTime.now()), HttpStatus.OK);
        }

        return new ResponseEntity<ApiResponseSuccess>(HttpStatus.NOT_IMPLEMENTED);
    }

}
