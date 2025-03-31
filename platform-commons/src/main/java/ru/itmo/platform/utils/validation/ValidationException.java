package ru.itmo.platform.utils.validation;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ValidationException extends RuntimeException {

  private static final String VALIDATION_FAILED_WITH_ERRORS = "Validation failed with errors";

  private final transient List<ValidationError> errors;

  private final transient MessageFormatter messageFormatter;

  public ValidationException(@NonNull List<ValidationError> errors) {
    this(errors, new DefaultMessageFormatter());
  }

  public ValidationException(@NonNull List<ValidationError> errors, @NonNull MessageFormatter messageFormatter) {
    this.errors = errors;
    this.messageFormatter = messageFormatter;
  }

  public ValidationException(ValidationMessage validationMessage) {
    this(List.of(new ValidationError(validationMessage)));
  }

  public ValidationException(ValidationMessage validationMessage, Object... args) {
    this(List.of(new ValidationError(validationMessage, args)));
  }

  public ValidationException(ValidationError error) {
    this(List.of(error));
  }

  public ValidationException(ValidationError error, Exception e) {
    super(e);
    this.errors = List.of(error);
    messageFormatter = new DefaultMessageFormatter();
  }

  @Override
  public String getMessage() {
    return messageFormatter.format(this);
  }

  public String getCode() {
    return errors.stream()
        .map(ValidationError::errorCode)
        .findFirst()
        .orElse("000000");
  }

}