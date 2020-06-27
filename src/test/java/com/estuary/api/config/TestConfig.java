package com.estuary.api.config;

import com.estuary.api.utils.HttpRequestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public HttpRequestUtils httpRequestUtils() {
        return new HttpRequestUtils();
    }
}
