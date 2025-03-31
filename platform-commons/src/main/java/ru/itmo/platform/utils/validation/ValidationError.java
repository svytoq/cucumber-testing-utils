package ru.itmo.platform.utils.validation;

public record ValidationError(String errorCode, String errorMessage, String errorDescription) {

  public ValidationError(ValidationMessage validationMessage) {
    this(
        validationMessage.getCode(),
        validationMessage.getMessage(),
        validationMessage.getDescription()
    );
  }

  public ValidationError(ValidationMessage validationMessage, Object... args) {
    this(
        validationMessage.getCode(),
        validationMessage.getMessage(),
        validationMessage.formatDescription(args)
    );
  }

}