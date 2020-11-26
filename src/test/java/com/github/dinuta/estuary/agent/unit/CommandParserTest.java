package com.github.dinuta.estuary.agent.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.dinuta.estuary.agent.exception.YamlConfigException;
import com.github.dinuta.estuary.agent.model.YamlConfig;
import com.github.dinuta.estuary.agent.utils.YamlConfigParser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommandParserTest {
    private final static String YAML_CONFIG = "config.yaml";

    @Test
    public void whenConfigIsCompleteThenTheCommandsListIsTheExpectedOne() throws IOException, YamlConfigException {
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();

        YamlConfig config = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        List<String> commandsList = new YamlConfigParser().getCommandsList(config);

        assertThat(commandsList.size()).isEqualTo(3);
    }

    @Test
    public void whenScriptIsMissingFromConfigThenException() throws IOException {
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();

        YamlConfig config = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        config.setScript(new ArrayList<>());

        assertThatThrownBy(() -> {
            new YamlConfigParser().getCommandsList(config);
        }).isInstanceOf(YamlConfigException.class)
                .hasMessageContaining("Mandatory section 'script' was not found or it was empty.");
    }

    @Test
    public void whenBeforeScriptIsMissingButScriptIsPresentFromConfigThenOK() throws IOException, YamlConfigException {
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        YamlConfig config = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        config.setBeforeScript(new ArrayList<>());

        List<String> commandsList = new YamlConfigParser().getCommandsList(config);

        assertThat(commandsList.size()).isEqualTo(2);
    }

    @Test
    public void whenAfterScriptIsMissingButScriptIsPresentFromConfigThenOK() throws IOException, YamlConfigException {
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        YamlConfig config = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        config.setAfterScript(new ArrayList<>());

        List<String> commandsList = new YamlConfigParser().getCommandsList(config);

        assertThat(commandsList.size()).isEqualTo(2);
    }

    @Test
    public void whenBeforeAndAfterScriptIsMissingButScriptIsPresentFromConfigThenOK() throws IOException, YamlConfigException {
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        YamlConfig config = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        config.setBeforeScript(new ArrayList<>());
        config.setAfterScript(new ArrayList<>());

        List<String> commandsList = new YamlConfigParser().getCommandsList(config);

        assertThat(commandsList.size()).isEqualTo(1);
    }

    @Test
    public void whenEnvSectionIsMissingButScriptIsPresentFromConfigThenOK() throws IOException, YamlConfigException {
        String yamlConfigString = IOUtils.toString(this.getClass().getResourceAsStream(YAML_CONFIG), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        YamlConfig config = objectMapper.readValue(yamlConfigString, YamlConfig.class);
        config.setEnv(new HashMap<>());

        List<String> commandsList = new YamlConfigParser().getCommandsList(config);

        assertThat(commandsList.size()).isEqualTo(3);
    }
}
