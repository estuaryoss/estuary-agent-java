package com.github.estuaryoss.agent.utils;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.util.Map;

public class TemplateGluer {

    public static String glue(String template, Map<String, String> swapValuesMap) {
        if (swapValuesMap == null || template == null) return template;

        Template jMustacheTemplate = Mustache.compiler().compile(template);

        return jMustacheTemplate.execute(swapValuesMap);
    }
}
