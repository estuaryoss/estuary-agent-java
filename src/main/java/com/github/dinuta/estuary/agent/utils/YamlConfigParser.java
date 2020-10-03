package com.github.dinuta.estuary.agent.utils;

import com.github.dinuta.estuary.agent.exception.YamlConfigException;
import com.github.dinuta.estuary.agent.model.YamlConfig;

import java.util.ArrayList;
import java.util.List;

public class YamlConfigParser {
    private static List<String> commandsList = new ArrayList<>();

    public static List<String> getCommandsList(YamlConfig yamlConfig) throws YamlConfigException {
        checkConfig(yamlConfig);
        commandsList.addAll(yamlConfig.getBefore_script());
        commandsList.addAll(yamlConfig.getScript());
        commandsList.addAll(yamlConfig.getAfter_script());

        return commandsList;
    }

    private static void checkConfig(YamlConfig yamlConfig) throws YamlConfigException {
        if (yamlConfig.getScript() == null)
            throw new YamlConfigException("Mandatory section 'script' was not found or it was empty.");

        if (yamlConfig.getScript().size() == 0)
            throw new YamlConfigException("Mandatory section 'script' was not found or it was empty.");
    }
}
