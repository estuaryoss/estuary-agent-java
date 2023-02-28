package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.component.AppEnvironment;
import com.github.estuaryoss.agent.constants.HeaderConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.UUID;

import static com.github.estuaryoss.agent.constants.EnvConstants.HTTP_AUTH_TOKEN;

@Component
@Slf4j
public class ApiOriginFilter extends GenericFilterBean {
    private final AppEnvironment environment;

    @Autowired
    public ApiOriginFilter(AppEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");

        String xRequestId = ((HttpServletRequest) request).getHeader(HeaderConstants.X_REQUEST_ID);
        String tokenHeader = ((HttpServletRequest) request).getHeader(HeaderConstants.TOKEN);

        if (xRequestId == null) {
            xRequestId = UUID.randomUUID().toString();
        }
        httpResponse.addHeader(HeaderConstants.X_REQUEST_ID, xRequestId);
        log.debug(HeaderConstants.TOKEN + " Header: " + tokenHeader);
        log.debug(HeaderConstants.X_REQUEST_ID + " : " + xRequestId);

        if (!(String.valueOf(tokenHeader).equals(String.valueOf(environment.getEnv().get(HTTP_AUTH_TOKEN))))) {
            httpResponse.sendError(HttpStatus.UNAUTHORIZED.value());

            return;
        }

        filterChain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {
    }
}
