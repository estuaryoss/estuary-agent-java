package com.github.dinuta.estuary.api;

import com.github.dinuta.estuary.constants.HeaderConstants;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.github.dinuta.estuary.constants.EnvConstants.HTTP_AUTH_TOKEN;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

@Component
public class ApiOriginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

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
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
