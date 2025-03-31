package ru.itmo.platform.utils.validation;

import java.util.Collection;
import java.util.List;

public interface ValidationRule<E> {

  default void validate(E target, List<ValidationError> errors) {
    // does nothing in case it shouldn't
  }

  default void validate(E active, E target, List<ValidationError> errors) {
    // does nothing in case it shouldn't
  }

  default void validate(Collection<E> target, List<ValidationError> errors) {
    target.forEach(t -> validate(t, errors));
  }

}