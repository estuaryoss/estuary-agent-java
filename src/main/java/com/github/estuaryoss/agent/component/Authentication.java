package com.github.estuaryoss.agent.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Authentication {
    @Value("${app.user:admin}")
    @Getter
    private String user;

    @Value("${app.password:secret}")
    @Getter
    private String password;
}
