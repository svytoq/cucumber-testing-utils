package ru.itmo.integration.kafka.listeners;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_KEY;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import ru.itmo.integration.kafka.model.KafkaMessage;


@Slf4j
@Getter
@RequiredArgsConstructor
public class TestKafkaListener {

  public static final long WAIT_FOR_EVENT_MILLIS = 2000;

  private final BlockingDeque<KafkaMessage> messages = new LinkedBlockingDeque<>(1024);
  private final String topic;

  @SneakyThrows
  @KafkaListener(
      topics = "#{__listener.topic}",
      groupId = "#{T(java.util.UUID).randomUUID().toString()}",
      properties = {
          "auto.offset.reset=earliest",
          "key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
          "value.deserializer=org.apache.kafka.common.serialization.StringDeserializer"
      }
  )
  public void receive(
      @Payload String message,
      @Headers Map<String, Object> headers
  ) {
    String key = extractKey(headers);
    log.info("[{}]: {} - {}", topic, key, message);
    messages.add(new KafkaMessage(key, message, headers));
  }

  protected String extractKey(Map<String, Object> headers) {
    return (String) headers.get(RECEIVED_KEY);
  }

  @SneakyThrows
  public String getEvent() {
    return valueOrNull(messages.poll(WAIT_FOR_EVENT_MILLIS, MILLISECONDS));
  }

  @SneakyThrows
  public String getEvent(long timeoutMillis) {
    return valueOrNull(messages.poll(timeoutMillis, MILLISECONDS));
  }

  @SneakyThrows
  public KafkaMessage getKafkaMessage(long timeoutMillis) {
    return messages.poll(timeoutMillis, MILLISECONDS);
  }

  public void clearEvents() {
    messages.clear();
  }

  @SneakyThrows
  public List<String> getAllEvents() {
    return messages.stream()
        .map(TestKafkaListener::valueOrNull)
        .toList();
  }

  private static String valueOrNull(KafkaMessage message) {
    return message == null ? null : message.getValue();
  }

}