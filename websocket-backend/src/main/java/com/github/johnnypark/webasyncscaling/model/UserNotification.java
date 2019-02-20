package com.github.johnnypark.webasyncscaling.model;

import java.io.Serializable;
import java.util.Objects;


public class UserNotification implements Serializable {

  private static final long serialVersionUID = 1123123123124L;

  private String name;

  private String message;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserNotification that = (UserNotification) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, message);
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


}
