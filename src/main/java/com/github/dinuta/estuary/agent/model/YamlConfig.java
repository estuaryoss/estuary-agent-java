package com.github.dinuta.estuary.agent.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlConfig {
    private Map<String, String> env = new LinkedHashMap<>();
    private List<String> before_script;
    private List<String> script;
    private List<String> after_script;


    public List<String> getBefore_script() {
        return before_script;
    }

    public void setBefore_script(List<String> before_script) {
        this.before_script = before_script;
    }

    public List<String> getScript() {
        return script;
    }

    public void setScript(List<String> script) {
        this.script = script;
    }

    public List<String> getAfter_script() {
        return after_script;
    }

    public void setAfter_script(List<String> after_script) {
        this.after_script = after_script;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }
}
