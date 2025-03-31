package ru.itmo.platform.utils.validation;

import java.util.Collection;
import org.apache.commons.lang3.tuple.Pair;

public interface Validator<E> {

  void validate(E target);

  void validate(E active, E target);

  void validateAll(Collection<E> target);

  void validateAllActiveTargetPairs(Collection<Pair<E, E>> active);

}