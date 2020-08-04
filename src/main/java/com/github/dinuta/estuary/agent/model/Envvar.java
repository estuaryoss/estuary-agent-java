package com.github.dinuta.estuary.agent.model;

import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Envvar
 */
@Validated
@javax.annotation.Generated(value = "com.github.dinuta.estuary.codegen.languages.SpringCodegen", date = "2020-06-14T18:02:50.529Z")

public class Envvar {

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Envvar {\n");

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

