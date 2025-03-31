package ru.itmo.integration.http.handler;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeaderHandler {

  private final ObjectMapper objectMapper;

  @SneakyThrows
  public void setHeaders(HttpHeaders headers, String body) {
    if (body == null) {
      headers.set("Content-Type", "application/json");
      return;
    }

    JsonNode requestJson = objectMapper.readTree(body);
    JsonNode authNode = requestJson.path("headers").path("Authorization");

    if (!authNode.isMissingNode()) {
      String authorizationToken = parseAuthorizationToken(authNode);
      headers.set("Authorization", "Bearer " + authorizationToken);
    } else {
      headers.set("Content-Type", "application/json");
    }
  }

  @SneakyThrows
  public String parseAuthorizationToken(JsonNode authNode) {
    String header = authNode.get("header").toString();
    String body = authNode.get("body").toString();
    String encodedHeader = Base64.getUrlEncoder().encodeToString(header.getBytes(UTF_8));
    String encodedBody = Base64.getUrlEncoder().encodeToString(body.getBytes(UTF_8));

    return encodedHeader + "." + encodedBody;
  }

}
