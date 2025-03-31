package ru.itmo.platform.utils.validation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

  public static void validateFieldNotNull(
      List<ValidationError> errors,
      Object value,
      ValidationMessage validationMessage,
      Object... args
  ) {
    if (value == null) {
      errors.add(new ValidationError(validationMessage, args));
    }
  }

  public static void validateFieldNotBlank(
      List<ValidationError> errors,
      String value,
      ValidationMessage validationMessage,
      Object... args
  ) {
    if (StringUtils.isBlank(value)) {
      errors.add(new ValidationError(validationMessage, args));
    }
  }

  public static void validateFieldMatches(
      List<ValidationError> errors,
      String value,
      String regex,
      ValidationMessage validationMessage,
      Object... args
  ) {
    if (StringUtils.isNotBlank(value) && !value.matches(regex)) {
      errors.add(new ValidationError(validationMessage, args));
    }
  }

  public static void validateFieldInRange(
      List<ValidationError> errors,
      Integer value,
      Integer min,
      Integer max,
      ValidationMessage validationMessage,
      Object... args
  ) {
    if (value < min || value > max) {
      errors.add(new ValidationError(validationMessage, args));
    }
  }

  public static void validateCollectionEmpty(
      List<ValidationError> errors,
      Collection<?> collection,
      ValidationMessage validationMessage,
      Object... args
  ) {
    if (CollectionUtils.isEmpty(collection)) {
      errors.add(new ValidationError(validationMessage, args));
    }
  }

  public static void validateStringsNotEqual(
      List<ValidationError> errors,
      String s1,
      String s2,
      ValidationMessage validationMessage
  ) {
    if ((s1 != null && s2 != null) && s1.equals(s2)) {
      errors.add(new ValidationError(validationMessage, s1, s2));
    }
  }

  public static void validateMaxLength(
      List<ValidationError> errors,
      String value,
      long maxLength,
      ValidationMessage validationMessage,
      Object... args
  ) {
    if (value != null && value.length() > maxLength) {
      errors.add(new ValidationError(validationMessage, args));
    }
  }

  public static void validateMaxSize(
      List<ValidationError> errors,
      Map<?, ?> map,
      long maxSize,
      ValidationMessage validationMessage,
      Object... args
  ) {
    if (map != null && map.size() > maxSize) {
      errors.add(new ValidationError(validationMessage, args));
    }
  }

}