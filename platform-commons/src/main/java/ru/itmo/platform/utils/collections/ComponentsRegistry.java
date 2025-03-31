package ru.itmo.platform.utils.collections;

import static java.util.stream.Collectors.toMap;
import static ru.itmo.platform.utils.collections.CollectionUtils.toLinkedMap;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ComponentsRegistry<K, T> {

  private final Map<K, T> components;
  private T defaultComponent;

  public ComponentsRegistry(Collection<T> components, Function<T, K> keyResolver) {
    this.components = toLinkedMap(components, keyResolver);
  }

  public ComponentsRegistry(Collection<T> components, Function<T, K> keyResolver, K defaultKey) {
    this.components = toLinkedMap(components, keyResolver);
    this.defaultComponent = this.components.get(defaultKey);
  }

  public <V> ComponentsRegistry(Map<K, V> components, Function<V, T> valueMapper) {
    this.components = components.entrySet().stream().collect(
        toMap(Entry::getKey, e -> valueMapper.apply(e.getValue()))
    );
  }

  public <V> ComponentsRegistry(Map<K, V> components, BiFunction<K, V, T> mapper) {
    this.components = components.entrySet().stream().collect(
        toMap(Entry::getKey, e -> mapper.apply(e.getKey(), e.getValue()))
    );
  }

  public ComponentsRegistry(Map<K, T> components) {
    this.components = components;
  }

  public T get(K key) {
    return Objects.requireNonNull(components.get(key),
        "Unknown key: " + key);
  }

  public T getOrNull(K key) {
    return components.get(key);
  }

  public T getOrDefault(K key) {
    return components.getOrDefault(key, defaultComponent);
  }

  public T getOrDefault(K key, T defaultValue) {
    return components.getOrDefault(key, defaultValue);
  }

  public Set<K> keys() {
    return components.keySet();
  }

  public Collection<T> values() {
    return components.values();
  }

  public ComponentsRegistry<K, T> mergeWith(Map<K, T> other) {
    Map<K, T> mergedComponents = new LinkedHashMap<>(this.components);
    mergedComponents.putAll(other);
    return new ComponentsRegistry<>(mergedComponents);
  }

  public <V> ComponentsRegistry<K, V> mapValues(Function<T, V> mapper) {
    return new ComponentsRegistry<>(components, mapper);
  }

  public Optional<T> getApplicable(Predicate<K> filter) {
    return components.entrySet().stream()
        .filter(e -> filter.test(e.getKey()))
        .map(Entry::getValue)
        .findFirst();
  }

}