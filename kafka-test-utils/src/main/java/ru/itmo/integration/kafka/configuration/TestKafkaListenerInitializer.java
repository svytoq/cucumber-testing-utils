package ru.itmo.integration.kafka.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import ru.itmo.integration.kafka.listeners.TestKafkaListener;


@Component
@RequiredArgsConstructor
public class TestKafkaListenerInitializer {

  private static final String LISTENER_BEAN_NAME_PREFIX = "TestKafkaListener_%s";

  private final GenericApplicationContext applicationContext;

  public TestKafkaListener registerListener(String topic) {
    String beanName = LISTENER_BEAN_NAME_PREFIX.formatted(topic);
    applicationContext.registerBean(beanName, TestKafkaListener.class, topic);

    return applicationContext.getBean(beanName, TestKafkaListener.class);
  }

}
