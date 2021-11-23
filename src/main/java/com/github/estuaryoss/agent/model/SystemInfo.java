package com.github.estuaryoss.agent.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemInfo {
    private String system;
    private String platform;
    private String release;
    private String version;
    private String architecture;
    private String machine;
    private String layer;
    private String hostname;
    private String cpu;
    private String ram;
    private String java;
}
