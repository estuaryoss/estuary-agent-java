package com.github.estuaryoss.agent.api;

import com.github.estuaryoss.agent.component.VirtualEnvironment;
import com.github.estuaryoss.agent.constants.HeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.github.estuaryoss.agent.constants.EnvConstants.HTTP_AUTH_TOKEN;

@Component
@Slf4j
public class ApiOriginFilter extends GenericFilterBean {
    @Autowired
    private VirtualEnvironment environment;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
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

        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {
    }
}
