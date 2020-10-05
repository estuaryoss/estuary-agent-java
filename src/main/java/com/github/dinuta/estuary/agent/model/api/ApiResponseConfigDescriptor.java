package com.github.dinuta.estuary.agent.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dinuta.estuary.agent.model.ConfigDescriptor;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

public class ApiResponseConfigDescriptor {
    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("description")
    private ConfigDescriptor configDescriptor = null;

    @JsonProperty("timestamp")
    private String timestamp = null;

    @JsonProperty("path")
    private String path = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("version")
    private String version = null;

    public ApiResponseConfigDescriptor code(int code) {
        this.code = code;
        return this;
    }

    public ApiResponseConfigDescriptor message(String message) {
        this.message = message;
        return this;
    }

    public ApiResponseConfigDescriptor configDescriptor(ConfigDescriptor configDescriptor) {
        this.configDescriptor = configDescriptor;
        return this;
    }

    public ApiResponseConfigDescriptor path(String path) {
        this.path = path;
        return this;
    }

    public ApiResponseConfigDescriptor timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ApiResponseConfigDescriptor name(String name) {
        this.name = name;
        return this;
    }

    public ApiResponseConfigDescriptor version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     **/
    @ApiModelProperty(value = "")
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Get message
     *
     * @return message
     **/
    @ApiModelProperty(value = "")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get timestamp
     *
     * @return timestamp
     **/
    @ApiModelProperty(value = "")
    @Valid
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ConfigDescriptor getConfigDescriptor() {
        return configDescriptor;
    }

    public void setConfigDescriptor(ConfigDescriptor configDescriptor) {
        this.configDescriptor = configDescriptor;
    }

    /**
     * Get name
     *
     * @return name
     **/
    @ApiModelProperty(value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get version
     *
     * @return version
     **/
    @ApiModelProperty(value = "")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiResponseConfigDescriptor apiResponseSuccess = (ApiResponseConfigDescriptor) o;
        return Objects.equals(this.message, apiResponseSuccess.message) &&
                Objects.equals(this.configDescriptor, apiResponseSuccess.configDescriptor) &&
                Objects.equals(this.code, apiResponseSuccess.code) &&
                Objects.equals(this.timestamp, apiResponseSuccess.timestamp) &&
                Objects.equals(this.path, apiResponseSuccess.path) &&
                Objects.equals(this.name, apiResponseSuccess.name) &&
                Objects.equals(this.version, apiResponseSuccess.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, configDescriptor, path, timestamp, name, version);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    code: ").append(toIndentedString(code));
        sb.append("    message: ").append(toIndentedString(message));
        sb.append("    description: ").append(toIndentedString(configDescriptor.toString()));
        sb.append("    path: ").append(toIndentedString(path));
        sb.append("    timestamp: ").append(toIndentedString(timestamp));
        sb.append("    name: ").append(toIndentedString(name));
        sb.append("    version: ").append(toIndentedString(version));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

