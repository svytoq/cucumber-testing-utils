package ru.itmo.integration.kafka.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaMessage {

  private String key;
  private String value;
  private Map<String, Object> headers;

}