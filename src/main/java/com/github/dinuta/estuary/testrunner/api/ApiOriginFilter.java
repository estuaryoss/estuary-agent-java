package com.github.dinuta.estuary.testrunner.api;

import com.github.dinuta.estuary.testrunner.constants.HeaderConstants;
import org.apache.catalina.connector.RequestFacade;
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

import static com.github.dinuta.estuary.testrunner.constants.EnvConstants.HTTP_AUTH_TOKEN;

@Component
public class ApiOriginFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");

        String tokenHeader = ((RequestFacade) request).getHeader(HeaderConstants.TOKEN);
        String xRequestId = ((RequestFacade) request).getHeader(HeaderConstants.X_REQUEST_ID);

        if (xRequestId == null) {
            xRequestId = UUID.randomUUID().toString();
        }
        httpResponse.addHeader(HeaderConstants.X_REQUEST_ID, xRequestId);

        if (!(String.valueOf(tokenHeader)
                .equals(String.valueOf(System.getenv(HTTP_AUTH_TOKEN))))) {
            httpResponse.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
