package ru.itmo.integration.kafka.configuration;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@ComponentScan("ru.itmo.integration.kafka")
public class TestKafkaConfiguration {

  @Bean("testProducerFactory")
  public ProducerFactory<String, String> testProducerFactory(
      @Value("${KAFKA_BOOTSTRAP_SERVERS}") String bootstrapServers) {
    Map<String, Object> config = new HashMap<>();

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    return new DefaultKafkaProducerFactory<>(config);
  }

  @Bean("testStringTemplate")
  public KafkaTemplate<String, String> stringTemplate(ProducerFactory<String, String> testProducerFactory) {
    return new KafkaTemplate<>(testProducerFactory);
  }

}
