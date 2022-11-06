package com.github.estuaryoss.agent.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class About {
    @Getter
    private final String appName = "Estuary-Agent";

    @Value("${app.version:1.0.0}")
    @Getter
    private String version;

}
