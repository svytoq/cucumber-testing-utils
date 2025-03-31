package ru.itmo.integration.kafka.facades;


import ru.itmo.integration.kafka.jsonassertions.JsonAssertions;
import ru.itmo.integration.kafka.listeners.TestKafkaListener;

public interface KafkaFacade {

  TestKafkaListener getListener();

  JsonAssertions getAssertions();

  default String getTopic() {
    return getListener().getTopic();
  }

}
