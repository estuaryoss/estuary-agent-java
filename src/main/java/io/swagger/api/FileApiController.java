package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.ApiResponseSuccess;
import io.swagger.model.Filecontent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

@Controller
public class FileApiController implements FileApi {

    private static final Logger log = LoggerFactory.getLogger(FileApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public FileApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponseSuccess> fileGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token, @ApiParam(value = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<ApiResponseSuccess>(objectMapper.readValue("{  \"code\" : \"code\",  \"name\" : \"name\",  \"description\" : \"description\",  \"time\" : \"2000-01-23T04:56:07.000+00:00\",  \"message\" : \"{}\",  \"version\" : \"version\"}", ApiResponseSuccess.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ApiResponseSuccess>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ApiResponseSuccess>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ApiResponseSuccess> filePut(@ApiParam(value = "The content of the file", required = true) @Valid @RequestBody Filecontent content, @ApiParam(value = "", required = true) @RequestHeader(value = "File-Path", required = true) String filePath, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<ApiResponseSuccess>(objectMapper.readValue("{  \"code\" : \"code\",  \"name\" : \"name\",  \"description\" : \"description\",  \"time\" : \"2000-01-23T04:56:07.000+00:00\",  \"message\" : \"{}\",  \"version\" : \"version\"}", ApiResponseSuccess.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ApiResponseSuccess>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ApiResponseSuccess>(HttpStatus.NOT_IMPLEMENTED);
    }

}
