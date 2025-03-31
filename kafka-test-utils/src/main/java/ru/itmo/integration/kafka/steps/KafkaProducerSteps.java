package ru.itmo.integration.kafka.steps;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.cucumber.java.en.When;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.core.io.ClassPathResource;
import ru.itmo.integration.kafka.mappers.HeadersValueMapper;
import ru.itmo.integration.kafka.producers.TestKafkaProducer;


@RequiredArgsConstructor
public class KafkaProducerSteps {

  private final TestKafkaProducer producer;
  private final main.java.ru.itmo.platform.utils.mapping.ObjectMapperHelper objectMapperHelper;
  private final HeadersValueMapper headersValueMapper;

  @When("External service sends message to kafka topic {string}")
  public void externalServiceSendsMessageToKafka(String topic, String message) {
    producer.send(topic, null, message, new RecordHeaders());
  }

  @When("External service sends message to kafka topic {string} with key {string}")
  public void externalServiceSendsMessageToKafkaWithKey(String topic, String key, String message) {
    producer.send(topic, key, message, new RecordHeaders());
  }

  @When("External service sends message to kafka topic {string} with key {string} with null body")
  public void externalServiceSendsMessageToKafkaWithKeyWithNullBody(String topic, String key) {
    producer.send(topic, key, null, new RecordHeaders());
  }

  @When("External service sends message to kafka topic {string} with headers {string}")
  public void externalServiceSendsMessageToKafkaWithHeaders(String topic, String headersJson, String message) {
    Map<String, String> headersMap = objectMapperHelper.parseMap(headersJson, String.class, String.class);

    List<Header> headers = headersMap.entrySet().stream()
        .map(h -> new RecordHeader(h.getKey(), h.getValue().getBytes()))
        .map(Header.class::cast)
        .toList();

    producer.send(topic, null, message, new RecordHeaders(headers));
  }

  @When("External service sends message to kafka topic {string} with headers")
  public void externalServiceSendsMessageToKafkaTopicWithHeaders(String topic, String headersWithValue) {
    Pair<String, String> pair = headersValueMapper.toHeadersValueWrapper(headersWithValue);

    externalServiceSendsMessageToKafkaWithHeaders(topic, pair.getLeft(), pair.getRight());
  }

  @When("External service sends message from file {string} to kafka topic {string} with key {string}")
  @SneakyThrows
  public void externalServiceSendsMessageFromFileToKafkaTopic(String filePath, String topicName, String key) {
    ClassPathResource resource = new ClassPathResource(filePath);
    String message;
    try (InputStream inputStream = resource.getInputStream()) {
      message = new String(inputStream.readAllBytes(), UTF_8);
    }
    producer.send(topicName, key, message, new RecordHeaders());
  }

  @When("External service sends message from file {string} to kafka topic {string} with key {string} with headers")
  @SneakyThrows
  public void externalServiceSendsMessageFromFileToKafkaTopicWithHeaders(String filePath, String topicName, String key,
      String headersJson) {
    ClassPathResource resource = new ClassPathResource(filePath);
    String message;
    try (InputStream inputStream = resource.getInputStream()) {
      message = new String(inputStream.readAllBytes(), UTF_8);
    }

    Map<String, String> headersMap = objectMapperHelper.parseMap(headersJson, String.class, String.class);

    List<Header> headers = headersMap.entrySet().stream()
        .map(h -> new RecordHeader(h.getKey(), h.getValue().getBytes()))
        .map(Header.class::cast)
        .toList();

    producer.send(topicName, key, message, new RecordHeaders(headers));
  }

}
