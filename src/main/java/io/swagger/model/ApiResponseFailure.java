package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.threeten.bp.OffsetDateTime;

import javax.validation.Valid;
import java.util.Objects;

/**
 * ApiResponseFailure
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

public class ApiResponseFailure {
  @JsonProperty("message")
  private String message = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("code")
  private String code = null;

  @JsonProperty("stacktrace")
  private String stacktrace = null;

  @JsonProperty("time")
  private OffsetDateTime time = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("version")
  private String version = null;

  public ApiResponseFailure message(String message) {
    this.message = message;
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

  public ApiResponseFailure description(String description) {
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

  public ApiResponseFailure code(String code) {
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

  public ApiResponseFailure stacktrace(String stacktrace) {
    this.stacktrace = stacktrace;
    return this;
  }

  /**
   * Get stacktrace
   *
   * @return stacktrace
   **/
  @ApiModelProperty(value = "")


  public String getStacktrace() {
    return stacktrace;
  }

  public void setStacktrace(String stacktrace) {
    this.stacktrace = stacktrace;
  }

  public ApiResponseFailure time(OffsetDateTime time) {
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

  public ApiResponseFailure name(String name) {
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

  public ApiResponseFailure version(String version) {
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
    ApiResponseFailure apiResponseFailure = (ApiResponseFailure) o;
    return Objects.equals(this.message, apiResponseFailure.message) &&
            Objects.equals(this.description, apiResponseFailure.description) &&
            Objects.equals(this.code, apiResponseFailure.code) &&
            Objects.equals(this.stacktrace, apiResponseFailure.stacktrace) &&
            Objects.equals(this.time, apiResponseFailure.time) &&
            Objects.equals(this.name, apiResponseFailure.name) &&
            Objects.equals(this.version, apiResponseFailure.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, description, code, stacktrace, time, name, version);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiResponseFailure {\n");

    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    stacktrace: ").append(toIndentedString(stacktrace)).append("\n");
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

