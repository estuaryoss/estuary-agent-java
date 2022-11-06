package com.github.estuaryoss.agent.component;

import com.github.estuaryoss.agent.utils.TemplateGluer;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
public class AppEnvironment {
    private static final Logger log = LoggerFactory.getLogger(AppEnvironment.class);
    private static final String EXT_ENV_VAR_PATH = "environment.properties";
    private final ImmutableMap<String, String> environment = ImmutableMap.copyOf(System.getenv());
    private Map<String, String> virtualEnvironment = new LinkedHashMap<>();

    public static final int VIRTUAL_ENVIRONMENT_MAX_SIZE = 1000;

    public AppEnvironment() {
        this.setExtraEnvVarsFromFile();
    }

    private void setExtraEnvVarsFromFile() {

        try (InputStream fileInputStream = new FileInputStream(Paths.get(".", EXT_ENV_VAR_PATH).toFile())) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            virtualEnvironment.putAll(properties.entrySet()
                    .stream()
                    .filter(elem -> !environment.containsKey(elem.getKey()))
                    .collect(Collectors.toMap(elem -> elem.getKey().toString(),
                            elem -> elem.getValue().toString())));
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
        }

        log.debug("External env vars read from file '" + EXT_ENV_VAR_PATH + "' are: " + new JSONObject(virtualEnvironment).toString());
    }

    public boolean setVirtualEnvVar(String envVarName, String envVarValue) {
        if (environment.containsKey(envVarName)) return false;
        if (virtualEnvironment.size() >= VIRTUAL_ENVIRONMENT_MAX_SIZE) return false;

        virtualEnvironment.put(envVarName, glueVirtualEnvVar(envVarValue));

        return true;
    }

    public Map<String, String> setVirtualEnvVars(Map<String, String> envVars) {
        Map<String, String> addedEnvVars = new LinkedHashMap<>();

        envVars.forEach((key, value) -> {
            if (this.setVirtualEnvVar(key, value)) {
                addedEnvVars.put(key, this.getVirtualEnvVar(key));
            }
        });

        return addedEnvVars;
    }

    /**
     * Gets the immutable environment variables from the System
     *
     * @return Map containing initial immutable env vars plus virtual env vars set by the user
     */
    public Map<String, String> getEnvAndVirtualEnv() {
        Map<String, String> systemAndExternalEnvVars = new LinkedHashMap<>();
        systemAndExternalEnvVars.putAll(environment);
        systemAndExternalEnvVars.putAll(virtualEnvironment);

        return systemAndExternalEnvVars;
    }

    /**
     * Gets the immutable environment variables from the System
     *
     * @return Map containing initial immutable env vars
     */
    public Map<String, String> getEnv() {
        return environment;
    }

    /**
     * Gets the virtual environment variables
     *
     * @return Map containing mutable env vars set by the user
     */
    public Map<String, String> getVirtualEnv() {
        return virtualEnvironment;
    }

    /**
     * Gets the virtual environment variable
     *
     * @return String containing the value of the env var set by the user
     */
    public String getVirtualEnvVar(String envVarName) {
        return glueVirtualEnvVar(virtualEnvironment.get(envVarName));
    }

    /**
     * Deletes all the custom env vars contained in the virtual environment
     */
    public void cleanVirtualEnv() {
        virtualEnvironment.clear();
    }

    private String glueVirtualEnvVar(String envVarValue) {
        return TemplateGluer.glue(envVarValue, this.getEnvAndVirtualEnv());
    }
}
