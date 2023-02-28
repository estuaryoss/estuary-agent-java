package com.github.estuaryoss.agent.utils;

import com.github.estuaryoss.agent.model.logging.ParentMessage;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class MessageDumper {
    private static final String MESSAGE = "message";
    private static final String REQUEST_URI = "Request-Uri";

    public static ParentMessage dumpRequest(ServletRequest request) throws IOException {
        ParentMessage parentMessage = new ParentMessage();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        HashMap<String, Object> body = new HashMap<>();
        Enumeration<String> headerNames = ((HttpServletRequest) request).getHeaderNames();
        for (String headerName : Collections.list(headerNames)) {
            headers.put(headerName, ((HttpServletRequest) request).getHeader(headerName));
        }

        headers.put(REQUEST_URI, ((HttpServletRequest) request).getRequestURI());
        body.put(MESSAGE, IOUtils.readLines(request.getInputStream(), Charset.forName("UTF-8")).stream().collect(Collectors.joining(System.lineSeparator())));

        parentMessage.setHeaders(headers);
        parentMessage.setBody(body);

        return parentMessage;
    }

    public static ParentMessage dumpRequest(HttpServletRequest request, Object body) {
        ParentMessage parentMessage = new ParentMessage();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        HashMap<String, Object> enrichedBody = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        for (String headerName : Collections.list(headerNames)) {
            headers.put(headerName, request.getHeader(headerName));
        }
        headers.put(REQUEST_URI, request.getRequestURI());
        enrichedBody.put(MESSAGE, body);

        parentMessage.setHeaders(headers);
        parentMessage.setBody(enrichedBody);

        return parentMessage;
    }

    public static ParentMessage dumpResponse(HttpServletResponse servletResponse, Object body) {
        ParentMessage parentMessage = new ParentMessage();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        Collection<String> headerNames = servletResponse.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, servletResponse.getHeader(headerName));
        }

        parentMessage.setHeaders(headers);
        parentMessage.setBody(body);

        return parentMessage;
    }

    public static ParentMessage dumpMessage(String message) {
        ParentMessage parentMessage = new ParentMessage();
        HashMap<String, String> body = new HashMap<>();
        body.put("message", message);

        parentMessage.setHeaders(new LinkedHashMap<String, String>());
        parentMessage.setBody(body);

        return parentMessage;
    }
}
