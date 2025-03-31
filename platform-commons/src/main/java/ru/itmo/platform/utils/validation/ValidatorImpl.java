package ru.itmo.platform.utils.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

@RequiredArgsConstructor
public class ValidatorImpl<E> implements Validator<E> {

  private final List<ValidationRule<E>> rules;
  private final MessageFormatter formatter;

  public ValidatorImpl(List<ValidationRule<E>> rules) {
    this(rules, new DefaultMessageFormatter());
  }

  @Override
  public void validate(E target) {
    validateAndThrowOnReject(errors -> rules.forEach(r -> r.validate(target, errors)));
  }

  @Override
  public void validate(E active, E target) {
    validateAndThrowOnReject(errors -> rules.forEach(r -> r.validate(active, target, errors)));
  }

  @Override
  public void validateAll(Collection<E> target) {
    validateAndThrowOnReject(errors -> rules.forEach(r -> r.validate(target, errors)));
  }

  @Override
  public void validateAllActiveTargetPairs(Collection<Pair<E, E>> pairs) {
    validateAndThrowOnReject(errors -> pairs.forEach(p -> rules.forEach(r ->
        r.validate(p.getLeft(), p.getRight(), errors)
    )));
  }

  protected void validateAndThrowOnReject(Consumer<List<ValidationError>> validator) throws ValidationException {
    List<ValidationError> errors = new ArrayList<>();

    validator.accept(errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors, formatter);
    }
  }

}