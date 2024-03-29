package com.github.estuaryoss.agent.component;

import com.github.estuaryoss.agent.utils.TemplateGluer;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AppEnvironment {
    private static final String EXT_ENV_VAR_PATH = "environment.properties";
    private final ImmutableMap<String, String> environment = ImmutableMap.copyOf(System.getenv());
    private final Map<String, String> virtualEnvironment = new LinkedHashMap<>();
    public static final int VIRTUAL_ENVIRONMENT_MAX_SIZE = 1000;

    public AppEnvironment() {
        this.setExtraEnvVarsFromFile();
    }

    public boolean setVirtualEnvVar(String envVarName, String envVarValue) {
        return setVirtualEnvVar(envVarName, envVarValue, true);
    }

    public Map<String, String> setVirtualEnvVars(Map<String, String> envVars) {
        Map<String, String> addedEnvVars = new LinkedHashMap<>();

        envVars.forEach((key, value) -> {
            if (this.setVirtualEnvVar(key, value, false)) {
                addedEnvVars.put(key, this.getVirtualEnvVar(key));
            }
        });
        this.reInterpolateVirtualEnv(addedEnvVars);

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
        return virtualEnvironment.get(envVarName);
    }

    /**
     * Deletes all the custom env vars contained in the virtual environment
     */
    public void cleanVirtualEnv() {
        virtualEnvironment.clear();
    }

    private boolean setVirtualEnvVar(String envVarName, String envVarValue, boolean interpolate) {
        if (environment.containsKey(envVarName)) return false;
        if (virtualEnvironment.containsKey(envVarName)) {
            virtualEnvironment.put(envVarName, glueVirtualEnvVar(envVarValue));
            if (interpolate) this.reInterpolateVirtualEnv(this.getVirtualEnv());

            return true;
        }

        if (virtualEnvironment.size() >= VIRTUAL_ENVIRONMENT_MAX_SIZE) return false;
        virtualEnvironment.put(envVarName, glueVirtualEnvVar(envVarValue));
        if (interpolate) this.reInterpolateVirtualEnv(this.getVirtualEnv());

        return true;
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
        } catch (FileNotFoundException e) {
            log.debug(ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            log.debug(ExceptionUtils.getStackTrace(e));
        }

        log.debug("External env vars read from file '" + EXT_ENV_VAR_PATH + "' are: " + new JSONObject(virtualEnvironment).toString());
    }

    private String glueVirtualEnvVar(String envVarValue) {
        return TemplateGluer.glue(envVarValue, this.getEnvAndVirtualEnv());
    }

    private void reInterpolateVirtualEnv(Map<String, String> virtualEnvironment) {
        virtualEnvironment.forEach((envVarName, envVarValue) -> {
            virtualEnvironment.put(envVarName, glueVirtualEnvVar(envVarValue));
        });
    }
}
