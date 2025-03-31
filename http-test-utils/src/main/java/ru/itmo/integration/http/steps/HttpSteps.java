package ru.itmo.integration.http.steps;

import static main.java.ru.itmo.platform.utils.mapping.MappingUtils.cast;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.itmo.integration.http.datatables.DataTableBuilder;
import ru.itmo.integration.http.handler.HeaderHandler;


@RequiredArgsConstructor
public class HttpSteps {

  private final TestRestTemplate restTemplate;
  private final HeaderHandler headerHandler;
  private final ObjectMapper objectMapper;
  private final Map<String, String> contextVariables = new HashMap<>();
  private final Map<String, DataTableBuilder<?>> dataTableBuilders;

  private ResponseEntity<String> response;

  @When("User sends {string} request with url {string}")
  public void userSendsHttpRequest(String method, String url, String body) {
    HttpHeaders headers = new HttpHeaders();
    headerHandler.setHeaders(headers, body);
    response = restTemplate.exchange(
        replacePlaceholders(url),
        parseHttpMethod(method),
        new HttpEntity<>(body, headers),
        String.class
    );
  }

  @When("User sends GET request with url {string}")
  public void userSendsGetHttpRequest(String url) {
    userSendsHttpRequest("GET", url, null);
  }

  @When("User sends DELETE request with url {string}")
  public void userSendsDeleteHttpRequest(String url) {
    userSendsHttpRequest("DELETE", url, null);
  }

  @SneakyThrows
  @Then("Server returns {string} list by node {string}")
  public void serviceSendsResponseWithList(String className, String node, DataTable dataTable) {
    assertResponse(className, getJsonNode(node), dataTable, true);
  }

  @SneakyThrows
  @Then("Server returns {string} list")
  public void serviceSendsResponseWithList(String className, DataTable dataTable) {
    serviceSendsResponseWithList(className, "/data", replacePlaceholders(dataTable));
  }

  @SneakyThrows
  @Then("Server returns {string}")
  public void serviceSendsResponse(String className, DataTable dataTable) {
    assertResponse(className, getJsonNode("/data"), replacePlaceholders(dataTable), false);
  }

  @SneakyThrows
  @When("Save value from response node {string} as variable {string}")
  public void saveVariableFromResponse(String jsonPath, String variableName) {
    JsonNode node = objectMapper.readTree(response.getBody());
    contextVariables.put(variableName, node.at(jsonPath).asText());
  }

  private HttpMethod parseHttpMethod(String method) {
    return switch (method.toUpperCase()) {
      case "GET" -> GET;
      case "POST" -> POST;
      case "PUT" -> PUT;
      case "DELETE" -> DELETE;
      default -> throw new IllegalArgumentException("Unsupported method: " + method);
    };
  }

  private DataTable replacePlaceholders(DataTable dataTable) {
    return DataTable.create(dataTable.asLists().stream()
        .map(row -> row.stream()
            .map(this::replacePlaceholders)
            .toList())
        .toList());
  }

  private String replacePlaceholders(String input) {
    if (input == null) {
      return null;
    }

    String result = input;
    for (Map.Entry<String, String> entry : contextVariables.entrySet()) {
      result = result.replace("{" + entry.getKey() + "}", entry.getValue());
    }

    return result;
  }

  @SneakyThrows
  private JsonNode getJsonNode(String path) {
    return objectMapper.readTree(response.getBody()).at(path);
  }

  @SneakyThrows
  private void assertResponse(String className, JsonNode jsonNode, DataTable target, boolean isList) {
    DataTableBuilder<?> builder = dataTableBuilders.get(className + "DataTableBuilder");
    Class<?> targetClass = getClassFromGeneric(builder);

    Object result = isList
        ? objectMapper.readValue(jsonNode.toString(), constructListType(targetClass))
        : objectMapper.treeToValue(jsonNode, targetClass);

    DataTable actual = isList
        ? builder.buildForList(cast(result))
        : builder.build(cast(result));

    actual.unorderedDiff(target);
  }

  private CollectionType constructListType(Class<?> elementClass) {
    return objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass);
  }

  @SuppressWarnings("rawtypes")
  private Class getClassFromGeneric(Object o) {
    ParameterizedType parameterizedType = (ParameterizedType) o.getClass().getGenericSuperclass();
    return (Class) parameterizedType.getActualTypeArguments()[0];
  }

}
