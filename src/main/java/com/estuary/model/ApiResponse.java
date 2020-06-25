package com.estuary.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ApiResponseSuccess
 */
@Validated
@javax.annotation.Generated(value = "com.estuary.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

public class ApiResponse {
    @JsonProperty("code")
    private String code = null;

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("description")
    private Object description = null;

    @JsonProperty("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime time = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("version")
    private String version = null;

    public ApiResponse message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     **/
    @ApiModelProperty(value = "")


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ApiResponse time(LocalDateTime time) {
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

  public ApiResponse description(Object description) {
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

  public void setDescription(String description) {
    this.description = description;
  }

  public ApiResponse code(String code) {
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
  }

  public ApiResponse name(String name) {
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

  public ApiResponse version(String version) {
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
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiResponse apiResponseSuccess = (ApiResponse) o;
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

    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("}");
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

