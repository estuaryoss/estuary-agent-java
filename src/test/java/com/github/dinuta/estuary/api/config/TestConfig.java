package com.github.dinuta.estuary.api.config;

import com.github.dinuta.estuary.api.utils.HttpRequestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public HttpRequestUtils httpRequestUtils() {
        return new HttpRequestUtils();
    }
}
