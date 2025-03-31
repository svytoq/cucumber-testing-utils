package ru.itmo.integration.kafka.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class HeadersValueMapper {

  private static final String HEADERS_NODE = "/headers";
  private static final String VALUE_NODE   = "/value";

  private final main.java.ru.itmo.platform.utils.mapping.ObjectMapperHelper objectMapperHelper;

  public Pair<String, String> toHeadersValueWrapper(String headersWithValue) {
    JsonNode rootNode = objectMapperHelper.readTree(headersWithValue);

    String headers = rootNode.at(HEADERS_NODE).toString();
    String value = rootNode.at(VALUE_NODE).toString();

    return Pair.of(headers, value);
  }

}