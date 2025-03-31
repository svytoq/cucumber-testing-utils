package ru.itmo.integration.kafka.facades;

import java.util.List;
import org.springframework.stereotype.Component;
import ru.itmo.platform.utils.collections.ComponentsRegistry;


@Component
public class KafkaFacadeRegistry extends ComponentsRegistry<String, KafkaFacade> {

  public KafkaFacadeRegistry(List<KafkaFacade> components) {
    super(components, KafkaFacade::getTopic);
  }

}