package ru.itmo.platform.utils.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils {

  public static <K, T> Map<K, T> toMap(Collection<T> source, Function<T, K> keyResolver) {
    return source.stream().collect(Collectors.toMap(keyResolver, a -> a));
  }

  public static <K, T> Map<K, T> toLinkedMap(Collection<T> source, Function<T, K> keyResolver) {
    return source.stream().collect(Collectors.toMap(keyResolver, a -> a,
        (a, b) -> {
          throw new IllegalStateException("Duplicate key");
        },
        LinkedHashMap::new));
  }

  public static Map<String, String> toLinkedStringMap(String... keyValuePairs) {
    Map<String, String> result = new LinkedHashMap<>();
    for (int i = 0; i < keyValuePairs.length / 2; i++) {
      String key = keyValuePairs[i * 2];
      String value = keyValuePairs[i * 2 + 1];
      if (value != null) {
        result.put(key, value);
      }
    }
    return result;
  }

  @SafeVarargs
  public static <T> Set<T> toLinkedSet(T... values) {
    return Arrays.stream(values)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public static <K, T> Set<K> toSet(Collection<T> source, Function<T, K> keyResolver) {
    return source.stream()
        .map(keyResolver)
        .collect(Collectors.toSet());
  }

  public static <T, U> Set<U> toSortedLinkedSet(Collection<T> source, Function<T, U> keyResolver) {
    return source.stream()
        .map(keyResolver)
        .sorted()
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public static String[] concatArrays(String[] array1, String... array2) {
    String[] result = Arrays.copyOf(array1, array1.length + array2.length);
    System.arraycopy(array2, 0, result, array1.length, array2.length);

    return result;
  }

  public static void validateListIsNotEmpty(List<?> list, String errorMessage) {
    if (list.isEmpty()) {
      throw new IllegalStateException(errorMessage);
    }
  }

  public static <T> String listToString(List<T> list, Function<T, String> keyResolver) {
    return list.stream()
        .map(keyResolver)
        .collect(Collectors.joining(",", "[", "]"));
  }

}