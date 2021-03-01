package com.github.estuaryoss.agent.api.config;

import com.github.estuaryoss.agent.api.utils.HttpRequestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public HttpRequestUtils httpRequestUtils() {
        return new HttpRequestUtils();
    }
}
