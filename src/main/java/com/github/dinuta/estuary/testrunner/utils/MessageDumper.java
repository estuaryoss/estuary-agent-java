package com.github.dinuta.estuary.testrunner.utils;

import com.github.dinuta.estuary.testrunner.model.logging.ParentMessage;
import org.apache.catalina.connector.RequestFacade;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MessageDumper {
    private static final String MESSAGE = "message";
    private static final String REQUEST_URI = "Request-Uri";

    public static ParentMessage dumpRequest(ServletRequest request) throws IOException {
        ParentMessage parrentMessage = new ParentMessage();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        HashMap<String, Object> body = new HashMap<>();
        Enumeration<String> headerNames = ((RequestFacade) request).getHeaderNames();
        for (String headerName : Collections.list(headerNames)) {
            headers.put(headerName, ((RequestFacade) request).getHeader(headerName));
        }

        headers.put(REQUEST_URI, ((RequestFacade) request).getRequestURI());
        body.put(MESSAGE, request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));

        parrentMessage.setHeaders(headers);
        parrentMessage.setBody(body);

        return parrentMessage;
    }

    public static ParentMessage dumpRequest(HttpServletRequest request, Object body) {
        ParentMessage parrentMessage = new ParentMessage();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        HashMap<String, Object> enrichedBody = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        for (String headerName : Collections.list(headerNames)) {
            headers.put(headerName, request.getHeader(headerName));
        }
        headers.put(REQUEST_URI, request.getRequestURI());
        enrichedBody.put(MESSAGE, body);

        parrentMessage.setHeaders(headers);
        parrentMessage.setBody(enrichedBody);

        return parrentMessage;
    }

    public static ParentMessage dumpResponse(HttpServletResponse servletResponse, Object body) {
        ParentMessage parrentMessage = new ParentMessage();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        Collection<String> headerNames = servletResponse.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, servletResponse.getHeader(headerName));
        }

        parrentMessage.setHeaders(headers);
        parrentMessage.setBody(body);

        return parrentMessage;
    }

    public ParentMessage dumpMessage(String message) {
        ParentMessage parrentMessage = new ParentMessage();
        HashMap<String, String> body = new HashMap<>();
        body.put("message", message);

        parrentMessage.setHeaders(new LinkedHashMap<String, String>());
        parrentMessage.setBody(body);

        return parrentMessage;
    }
}
