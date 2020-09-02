package com.github.dinuta.estuary.agent.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class RequestUtil {
    private final HttpServletRequest httpServletRequest;

    public RequestUtil(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public String getRequestUri() {
        String fullRequestUri = httpServletRequest.getRequestURI() + "?";
        if (httpServletRequest.getQueryString() != null)
            return fullRequestUri + httpServletRequest.getQueryString();

        return fullRequestUri;
    }

    public HttpServletRequest getRequest() {
        return httpServletRequest;
    }
}
