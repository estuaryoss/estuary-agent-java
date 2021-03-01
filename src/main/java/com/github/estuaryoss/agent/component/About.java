package com.github.estuaryoss.agent.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class About {
    @Getter
    private final String appName = "estuary-agent";

    @Value("${app.version}")
    @Getter
    private String version;

}
