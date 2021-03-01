package com.github.estuaryoss.agent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YamlConfig {

    @JsonProperty("env")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Map<String, String> env = new LinkedHashMap<>();

    @JsonProperty("before_install")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> beforeInstall = new ArrayList<>();

    @JsonProperty("install")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> install = new ArrayList<>();

    @JsonProperty("after_install")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> afterInstall = new ArrayList<>();

    @JsonProperty("before_script")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> beforeScript = new ArrayList<>();

    @JsonProperty("script")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> script = new ArrayList<>();

    @JsonProperty("after_script")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<String> afterScript = new ArrayList<>();

    public YamlConfig() {
    }

    public YamlConfig(String config) {
    }

    public List<String> getAfterScript() {
        return afterScript;
    }

    public void setAfterScript(List<String> afterScript) {
        this.afterScript = afterScript;
    }

    public List<String> getBeforeScript() {
        return beforeScript;
    }

    public void setBeforeScript(List<String> beforeScript) {
        this.beforeScript = beforeScript;
    }

    public List<String> getScript() {
        return script;
    }

    public void setScript(List<String> script) {
        this.script = script;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public List<String> getBeforeInstall() {
        return beforeInstall;
    }

    public void setBeforeInstall(List<String> beforeInstall) {
        this.beforeInstall = beforeInstall;
    }

    public List<String> getInstall() {
        return install;
    }

    public void setInstall(List<String> install) {
        this.install = install;
    }

    public List<String> getAfterInstall() {
        return afterInstall;
    }

    public void setAfterInstall(List<String> afterInstall) {
        this.afterInstall = afterInstall;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    env: ").append(toIndentedString(env));
        sb.append("    before_install: ").append(toIndentedString(beforeInstall));
        sb.append("    install: ").append(toIndentedString(install));
        sb.append("    after_install: ").append(toIndentedString(afterInstall));
        sb.append("    before_script: ").append(toIndentedString(beforeScript));
        sb.append("    script: ").append(toIndentedString(script));
        sb.append("    after_script: ").append(toIndentedString(afterScript));
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
