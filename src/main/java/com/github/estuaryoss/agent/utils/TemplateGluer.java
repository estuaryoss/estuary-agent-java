package com.github.estuaryoss.agent.utils;

import java.util.Map;

public class TemplateGluer {
    private static final int RUN_DEPTH = 5;

    public static String glue(String template, Map<String, String> swapValuesMap) {
        if (swapValuesMap == null || template == null) return template;

        for (int i = 0; i < RUN_DEPTH; i++) {
            for (Map.Entry<String, String> entry : swapValuesMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                template = template.replace(String.format("{%s}", key), value);
                template = template.replace(String.format("{ %s }", key), value);
            }
        }

        return template;
    }
}
