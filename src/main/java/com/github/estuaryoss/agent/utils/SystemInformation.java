package com.github.estuaryoss.agent.utils;

import com.github.estuaryoss.agent.model.SystemInfo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemInformation {
    private static final Logger log = LoggerFactory.getLogger(SystemInformation.class);

    public static final SystemInfo getSystemInfo() {
        String layer = new File("/.dockerenv").exists() ? "Docker" : "Machine";
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024 * 1024);

        SystemInfo systemInfo = SystemInfo.builder()
                .system(getSystem())
                .platform("NA")
                .release("NA")
                .version(System.getProperty("os.version"))
                .architecture(System.getProperty("os.arch"))
                .machine("NA")
                .layer(layer)
                .hostname(getHostname())
                .cpu(System.getenv("PROCESSOR_IDENTIFIER") != null ? System.getenv("PROCESSOR_IDENTIFIER") : "NA")
                .ram(maxMemory + " GB")
                .java(System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version"))
                .build();

        return systemInfo;
    }

    public static String getHostname() {
        String hostname = "NA";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.info("Could not detect hostname: " + ExceptionUtils.getStackTrace(e));
        }

        return hostname;
    }

    private static String getSystem() {
        String platformName = System.getProperty("os.name").toLowerCase();
        if (platformName.contains("win")) return "Windows";
        if (platformName.contains("lin")) return "Linux";

        return "GenericOs";
    }
}