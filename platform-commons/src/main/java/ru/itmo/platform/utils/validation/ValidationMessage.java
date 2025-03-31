package ru.itmo.platform.utils.validation;

public interface ValidationMessage {

  String getCode();

  String getMessage();

  String getDescription();

  default String formatDescription(Object... args) {
    return String.format(getDescription(), args);
  }

}