package com.github.dinuta.estuary.agent.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

public class ApiResponseCommandDescription {
    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("description")
    private CommandDescription description = null;

    @JsonProperty("time")
    private String time = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("version")
    private String version = null;

    public ApiResponseCommandDescription message(String message) {
        this.message = message;
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

    public ApiResponseCommandDescription time(String time) {
        this.time = time;
        return this;
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

    public ApiResponseCommandDescription description(CommandDescription description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     **/
    @ApiModelProperty(value = "")


    public Object getDescription() {
        return description;
    }

    public void setDescription(CommandDescription description) {
        this.description = description;
    }

    public ApiResponseCommandDescription code(int code) {
        this.code = code;
        return this;
    }

    /**
     * Get time
     *
     * @return time
     **/
    @ApiModelProperty(value = "")

    @Valid

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ApiResponseCommandDescription name(String name) {
        this.name = name;
        return this;
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

    public ApiResponseCommandDescription version(String version) {
        this.version = version;
        return this;
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
        ApiResponseCommandDescription apiResponseSuccess = (ApiResponseCommandDescription) o;
        return Objects.equals(this.message, apiResponseSuccess.message) &&
                Objects.equals(this.description, apiResponseSuccess.description) &&
                Objects.equals(this.code, apiResponseSuccess.code) &&
                Objects.equals(this.time, apiResponseSuccess.time) &&
                Objects.equals(this.name, apiResponseSuccess.name) &&
                Objects.equals(this.version, apiResponseSuccess.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, description, code, time, name, version);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("    message: ").append(toIndentedString(message));
        sb.append("    description: ").append(toIndentedString(description.toString()));
        sb.append("    code: ").append(toIndentedString(code));
        sb.append("    time: ").append(toIndentedString(time));
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
}

