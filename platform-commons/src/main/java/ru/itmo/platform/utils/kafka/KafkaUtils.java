package ru.itmo.platform.utils.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.security.scram.internals.ScramFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaUtils {

  public static Map<String, String> toRequestHeaders(Headers headers, String filterPrefix) {
    return Arrays.stream(headers.toArray())
        .filter(h -> h.key().startsWith(filterPrefix))
        .collect(Collectors.toMap(Header::key, h -> new String(h.value(), StandardCharsets.UTF_8)));
  }

  public static List<Header> toKafkaHeaders(@NonNull Map<String, String> headers) {
    return headers.entrySet().stream()
        .map(entry -> (Header) new RecordHeader(entry.getKey(), ScramFormatter.toBytes(entry.getValue())))
        .toList();
  }

  public static String getHeaderValue(Headers headers, String headerKey) {
    return StreamSupport.stream(headers.spliterator(), false)
        .filter(header -> header.key().equals(headerKey))
        .map(Header::value)
        .map(String::new)
        .findFirst()
        .orElse(null);
  }

}