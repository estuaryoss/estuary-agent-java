package com.github.estuaryoss.agent.configuration;

import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.model.api.CommandParallel;
import com.github.estuaryoss.agent.service.Fluentd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Autowired
    private About about;

    @Bean
    public CommandParallel getCommandParallel() {
        return new CommandParallel();
    }

    @Bean
    public Fluentd getFluentdService() {
        return new Fluentd(about);
    }
}
