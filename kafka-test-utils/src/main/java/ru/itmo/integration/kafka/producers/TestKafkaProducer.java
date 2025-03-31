package ru.itmo.integration.kafka.producers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestKafkaProducer {

  private final KafkaOperations<String, String> stringTemplate;

  public TestKafkaProducer(
      @Qualifier("testStringTemplate") KafkaOperations<String, String> stringTemplate
  ) {
    this.stringTemplate = stringTemplate;
  }

  @SneakyThrows
  public void send(String topic, String key, String value, RecordHeaders headers) {

    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
        topic,
        null,
        key,
        value,
        headers
    );

    stringTemplate.send(producerRecord).get();
  }

}
