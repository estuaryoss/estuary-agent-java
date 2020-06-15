package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.threeten.bp.OffsetDateTime;

import javax.validation.Valid;
import java.util.Objects;

/**
 * ApiResponseSuccess
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

public class ApiResponseSuccess {
  @JsonProperty("message")
  private Object message = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("code")
  private String code = null;

  @JsonProperty("time")
  private OffsetDateTime time = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("version")
  private String version = null;

  public ApiResponseSuccess message(Object message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   *
   * @return message
   **/
  @ApiModelProperty(value = "")


  public Object getMessage() {
    return message;
  }

  public void setMessage(Object message) {
    this.message = message;
  }

  public ApiResponseSuccess description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   *
   * @return description
   **/
  @ApiModelProperty(value = "")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ApiResponseSuccess code(String code) {
    this.code = code;
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

  public ApiResponseSuccess time(OffsetDateTime time) {
    this.time = time;
    return this;
  }

  /**
   * Get time
   *
   * @return time
   **/
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime(OffsetDateTime time) {
    this.time = time;
  }

  public ApiResponseSuccess name(String name) {
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

  public ApiResponseSuccess version(String version) {
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
    ApiResponseSuccess apiResponseSuccess = (ApiResponseSuccess) o;
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
    sb.append("class ApiResponseSuccess {\n");

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

