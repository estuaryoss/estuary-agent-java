package com.github.dinuta.estuary.agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class ConfigDescriptor {
    @JsonProperty("config")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private YamlConfig yamlConfig = new YamlConfig();

    @JsonProperty("description")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private Object description = new Object();

    public YamlConfig getYamlConfig() {
        return yamlConfig;
    }

    public void setYamlConfig(YamlConfig yamlConfig) {
        this.yamlConfig = yamlConfig;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    config: ").append(toIndentedString(yamlConfig));
        sb.append("    description: ").append(toIndentedString(description));
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
