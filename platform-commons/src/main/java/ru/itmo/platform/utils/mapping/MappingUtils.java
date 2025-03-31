package ru.itmo.platform.utils.mapping;

import static lombok.AccessLevel.PRIVATE;

import java.util.function.Function;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class MappingUtils {

  public static <T> T parseOrNull(Object value, Function<String, T> mapper) {
    return value == null ? null : mapper.apply((String) value);
  }

  @SuppressWarnings("unchecked")
  public static  <T> T cast(Object object) {
    return (T) object;
  }

}