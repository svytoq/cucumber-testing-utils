package ru.itmo.platform.utils.validation;

import java.util.stream.Collectors;
import lombok.NonNull;

public class DefaultMessageFormatter implements MessageFormatter {

  @Override
  public String format(@NonNull ValidationException exception) {
    return exception.getErrors().stream()
        .map(ValidationError::errorDescription)
        .collect(Collectors.joining("; "));
  }

}
