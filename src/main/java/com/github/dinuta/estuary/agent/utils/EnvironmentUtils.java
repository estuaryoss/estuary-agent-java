package com.github.dinuta.estuary.agent.utils;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class EnvironmentUtils {
    private static final Logger log = LoggerFactory.getLogger(EnvironmentUtils.class);

    private static final String EXT_ENV_VAR_PATH = "environment.properties";
    private static ImmutableMap<String, String> environment = ImmutableMap.copyOf(System.getenv());

    public static Map<String, String> getExtraEnvVarsFromFile() {
        Map<String, String> envVars = new TreeMap<>();

        try (InputStream fileInputStream = new FileInputStream(Paths.get(".", EXT_ENV_VAR_PATH).toFile())) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            envVars.putAll(properties.entrySet()
                    .stream()
                    .collect(Collectors.toMap(elem -> elem.getKey().toString(),
                            elem -> elem.getValue().toString())));
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
        }

        log.debug("External env vars read from file '" + EXT_ENV_VAR_PATH + "' are: " + new JSONObject(envVars).toString());
        return envVars;
    }

    public static Map<String, String> getEnvironmentWithExternalEnvVars() {
        Map<String, String> systemAndExternalEnvVars = new LinkedHashMap<>();
        systemAndExternalEnvVars.putAll(environment);
        systemAndExternalEnvVars.putAll(getExtraEnvVarsFromFile());

        return systemAndExternalEnvVars;
    }

    public static Map<String, String> getEnvironment() {
        return environment;
    }
}
