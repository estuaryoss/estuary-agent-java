package com.github.dinuta.estuary.agent.utils;

import com.github.dinuta.estuary.agent.exception.YamlConfigException;
import com.github.dinuta.estuary.agent.model.YamlConfig;

import java.util.ArrayList;
import java.util.List;

public class YamlConfigParser {
    private List<String> commandsList = new ArrayList<>();

    public List<String> getCommandsList(YamlConfig yamlConfig) throws YamlConfigException {
        checkConfig(yamlConfig);
        commandsList.addAll(yamlConfig.getBeforeInstall());
        commandsList.addAll(yamlConfig.getInstall());
        commandsList.addAll(yamlConfig.getAfterInstall());
        commandsList.addAll(yamlConfig.getBeforeScript());
        commandsList.addAll(yamlConfig.getScript());
        commandsList.addAll(yamlConfig.getAfterScript());

        return commandsList;
    }

    private static void checkConfig(YamlConfig yamlConfig) throws YamlConfigException {
        if (yamlConfig.getScript().size() == 0)
            throw new YamlConfigException("Mandatory section 'script' was not found or it was empty.");
    }
}
