package ru.itmo.integration.kafka.jsonassertions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.SneakyThrows;
import ru.itmo.integration.kafka.model.KafkaMessage;


public abstract class JsonAssertionsImpl implements JsonAssertions {

  protected final main.java.ru.itmo.platform.utils.mapping.ObjectMapperHelper objectMapperHelper;
  private final Set<String> incomparableParams;

  protected JsonAssertionsImpl(main.java.ru.itmo.platform.utils.mapping.ObjectMapperHelper objectMapperHelper) {
    this(objectMapperHelper, Set.of());
  }

  protected JsonAssertionsImpl(main.java.ru.itmo.platform.utils.mapping.ObjectMapperHelper objectMapperHelper, Set<String> incomparableParams) {
    this.objectMapperHelper = objectMapperHelper;
    this.incomparableParams = incomparableParams;
  }

  @Override
  public void assertJsonEquals(String expectedJson, String actualJson) {
    assertNotNull(actualJson);
    assertEquals(toJsonNode(expectedJson), toJsonNode(actualJson));
  }

  @Override
  public void assertJsonAndHeadersEquals(String expectedValueJson, String expectedHeadersJson,
      KafkaMessage kafkaMessage) {
    assertJsonEquals(expectedValueJson, kafkaMessage.getValue());
    assertHeadersEquals(expectedHeadersJson, kafkaMessage);
  }

  @SneakyThrows
  public boolean jsonEquals(String expected, String actual) {
    JsonNode expectedJson = toJsonNode(expected);
    JsonNode actualJson = toJsonNode(actual);

    return expectedJson.equals(actualJson);
  }

  private JsonNode toJsonNode(String json) {
    JsonNode jsonNode = objectMapperHelper.readTree(json);

    removeFields(jsonNode, incomparableParams);

    return jsonNode;
  }

  private void removeFields(JsonNode node, Set<String> fieldNames) {
    if (node.isObject()) {
      ObjectNode objectNode = (ObjectNode) node;
      fieldNames.forEach(fieldName -> {
        if (objectNode.has(fieldName)) {
          objectNode.remove(fieldName);
        }
        objectNode.fields().forEachRemaining(entry -> removeFields(entry.getValue(), fieldNames));
      });
    } else if (node.isArray()) {
      node.forEach(arrayElement -> removeFields(arrayElement, fieldNames));
    }
  }

  @SneakyThrows
  protected void assertHeadersEquals(String expectedHeadersJson, KafkaMessage kafkaMessage) {
    Map<String, String> expectedHeaders = objectMapperHelper.parseMap(expectedHeadersJson, String.class, String.class);

    for (Entry<String, Object> entry : kafkaMessage.getHeaders().entrySet()) {
      if (expectedHeaders.containsKey(entry.getKey())) {
        assertEquals(expectedHeaders.get(entry.getKey()), new String((byte[]) entry.getValue()));
      }
    }
  }

}
