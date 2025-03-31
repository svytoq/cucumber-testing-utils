package ru.itmo.platform.utils.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectMapperHelper {

  private final ObjectMapper objectMapper;

  @SneakyThrows
  public String writeValueAsString(Object value) {
    return objectMapper.writeValueAsString(value);
  }

  @SneakyThrows
  public <T> T readValue(String payload, Class<T> itemClass) {
    return objectMapper.readValue(payload, itemClass);
  }

  public <T> T convertValue(Object fromValue, Class<T> toValueType) {
    return objectMapper.convertValue(fromValue, toValueType);
  }

  @SneakyThrows
  public <T> List<T> parseList(String payload, Class<T> itemClass) {
    CollectionType type = objectMapper.getTypeFactory().constructCollectionType(List.class, itemClass);
    return objectMapper.readValue(payload, type);
  }

  @SneakyThrows
  public <K, V> Map<K, V> parseMap(String payload, Class<K> keyClass, Class<V> valueClass) {
    MapType type = objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
    return objectMapper.readValue(payload, type);
  }

  @SneakyThrows
  public JsonNode readTree(String jsonMessage) {
    return objectMapper.readTree(jsonMessage);
  }

}