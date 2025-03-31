package ru.itmo.integration.kafka.jsonassertions;


import ru.itmo.integration.kafka.model.KafkaMessage;

public interface JsonAssertions {

  boolean jsonEquals(String expectedJson, String actualJson);

  void assertJsonEquals(String expectedJson, String actualJson);

  void assertJsonAndHeadersEquals(String expectedValueJson, String expectedHeadersJson, KafkaMessage kafkaMessage);

}