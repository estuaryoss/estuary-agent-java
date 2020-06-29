package com.github.dinuta.estuary.utils;

import org.apache.catalina.connector.RequestFacade;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MessageDumper {
    private static final String HEADERS = "headers";
    private static final String BODY = "body";
    private static final String MESSAGE = "message";
    private static final String REQUEST_URI = "Request-Uri";

    public static Map<String, Object> dumpRequest(ServletRequest request) throws IOException {
        Map<String, Object> dumpedRequest = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        Enumeration<String> headerNames = ((RequestFacade) request).getHeaderNames();
        for (String headerName : Collections.list(headerNames)) {
            headers.put(headerName, ((RequestFacade) request).getHeader(headerName));
        }
        headers.put(REQUEST_URI, ((RequestFacade) request).getRequestURI());
        body.put(MESSAGE, request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));

        dumpedRequest.put(BODY, body);
        dumpedRequest.put(HEADERS, headers);
        return dumpedRequest;
    }

    public static Map<String, Object> dumpRequest(HttpServletRequest request, Object body) {
        Map<String, Object> dumpedResponse = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        Map<String, Object> enrichedBody = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        for (String headerName : Collections.list(headerNames)) {
            headers.put(headerName, request.getHeader(headerName));
        }
        headers.put(REQUEST_URI, request.getRequestURI());
        enrichedBody.put(MESSAGE, body);

        dumpedResponse.put(BODY, enrichedBody);
        dumpedResponse.put(HEADERS, headers);
        return dumpedResponse;
    }

    public static Map<String, Object> dumpResponse(HttpServletResponse servletResponse, Object body) {
        Map<String, Object> dumpedResponse = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        Map<String, Object> enrichedBody = new HashMap<>();
        Collection<String> headerNames = servletResponse.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, servletResponse.getHeader(headerName));
        }
        enrichedBody.put(MESSAGE, body);

        dumpedResponse.put(BODY, enrichedBody);
        dumpedResponse.put(HEADERS, headers);
        return dumpedResponse;
    }

    public Map<String, Object> dumpMessage(String message) {
        Map<String, Object> dumpedMessage = new HashMap<>();
        dumpedMessage.put("body", message);
        dumpedMessage.put("headers", new HashMap<String, String>());

        return dumpedMessage;
    }
}
