package ru.itmo.integration.kafka.facades;

import java.util.Set;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itmo.integration.kafka.configuration.TestKafkaListenerInitializer;
import ru.itmo.integration.kafka.jsonassertions.JsonAssertions;
import ru.itmo.integration.kafka.jsonassertions.JsonAssertionsImpl;
import ru.itmo.integration.kafka.listeners.TestKafkaListener;


public class KafkaFacadeFactoryBean implements FactoryBean<KafkaFacade> {

  private final String topic;
  private final Set<String> incomparableParams;

  @Autowired
  private TestKafkaListenerInitializer listenerInitializer;
  @Autowired
  private main.java.ru.itmo.platform.utils.mapping.ObjectMapperHelper objectMapperHelper;

  public KafkaFacadeFactoryBean(String topic) {
    this(topic, Set.of());
  }

  public KafkaFacadeFactoryBean(String topic, Set<String> incomparableParams) {
    this.topic = topic;
    this.incomparableParams = incomparableParams;
  }

  @Override
  public KafkaFacade getObject() {
    TestKafkaListener listener = listenerInitializer.registerListener(topic);
    JsonAssertions assertions = new JsonAssertionsImpl(objectMapperHelper, incomparableParams) { };

    return new KafkaFacade() {
      @Override
      public TestKafkaListener getListener() {
        return listener;
      }

      @Override
      public JsonAssertions getAssertions() {
        return assertions;
      }
    };
  }

  @Override
  public Class<?> getObjectType() {
    return KafkaFacade.class;
  }

}
