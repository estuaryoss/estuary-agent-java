package com.github.estuaryoss.agent.configuration;

import com.github.estuaryoss.agent.component.About;
import com.github.estuaryoss.agent.model.BackgroundStateHolder;
import com.github.estuaryoss.agent.model.api.CommandParallel;
import com.github.estuaryoss.agent.service.FluentdService;
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
    public BackgroundStateHolder getStateHolder() {
        return new BackgroundStateHolder();
    }

    @Bean
    public FluentdService getFluentdService() {
        return new FluentdService(about);
    }
}
