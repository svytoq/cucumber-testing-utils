package ru.itmo.integration.kafka.steps;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.awaitility.core.ConditionTimeoutException;
import ru.itmo.integration.kafka.facades.KafkaFacade;
import ru.itmo.integration.kafka.facades.KafkaFacadeRegistry;
import ru.itmo.integration.kafka.jsonassertions.JsonAssertions;
import ru.itmo.integration.kafka.listeners.TestKafkaListener;
import ru.itmo.integration.kafka.mappers.HeadersValueMapper;
import ru.itmo.integration.kafka.model.KafkaMessage;


@RequiredArgsConstructor
public class KafkaListenerSteps {

  private final HeadersValueMapper headersValueMapper;
  private final KafkaFacadeRegistry kafkaFacadeRegistry;

  @Then("Kafka topic {string} receives message in {long} millis")
  public void kafkaTopicReceivesMessage(String topic, long timeoutMillis, String expected) {
    kafkaTopicReceivesMessage(topic, null, timeoutMillis, expected);
  }

  @Then("Kafka topic {string} receives message with headers {string} in {long} millis")
  public void kafkaTopicReceivesMessage(String topic, String expectedHeaders, long timeoutMillis,
      String expectedValue) {
    KafkaFacade kafkaFacade = this.kafkaFacadeRegistry.get(topic);
    JsonAssertions assertions = kafkaFacade.getAssertions();
    TestKafkaListener listener = kafkaFacade.getListener();

    AtomicReference<KafkaMessage> actualKafkaMessageRef = new AtomicReference<>();

    waitForMessage(timeoutMillis, expectedValue, listener, actualKafkaMessageRef, assertions);

    KafkaMessage actualKafkaMessage = actualKafkaMessageRef.get();

    if (expectedHeaders != null) {
      assertions.assertJsonAndHeadersEquals(expectedValue, expectedHeaders, actualKafkaMessage);
    } else {
      assertions.assertJsonEquals(expectedValue, actualKafkaMessage.getValue());
    }
  }

  @Then("Kafka topic {string} receives message with headers in {int} millis")
  public void kafkaTopicReceivesMessage(String topic, int timeoutMillis, String headersWithValue) {
    Pair<String, String> pair = headersValueMapper.toHeadersValueWrapper(headersWithValue);

    kafkaTopicReceivesMessage(topic, pair.getLeft(), timeoutMillis, pair.getRight());
  }

  @SuppressWarnings("java:S5960")
  @Then("Kafka topic {string} receives exact messages in {long} millis")
  public void kafkaTopicReceivesExactMessages(String topic, long timeoutMillis, String expected) {
    KafkaFacade kafkaFacade = kafkaFacadeRegistry.get(topic);
    TestKafkaListener listener = kafkaFacade.getListener();
    JsonAssertions assertions = kafkaFacade.getAssertions();

    Set<String> expectedJsons = new HashSet<>(Arrays.asList(expected.split("###")));
    Set<String> actual = new HashSet<>();

    await()
        .atMost(timeoutMillis, MILLISECONDS)
        .pollInterval(100, MILLISECONDS)
        .until(() -> {
          String event = listener.getEvent(10);
          if (event != null) {
            actual.add(event);
          }
          return actual.size() >= expectedJsons.size();
        });

    long unmatchedCount = expectedJsons.stream()
        .filter(e -> actual.stream().noneMatch(a -> assertions.jsonEquals(e, a)))
        .count();

    assertEquals(0, unmatchedCount, "Messages do not match:\n    Actual: " + actual);
    assertTrue(listener.getAllEvents().isEmpty());
  }

  @And("Kafka topic {string} is clear")
  public void kafkaTopicIsClear(String topic) {
    kafkaFacadeRegistry.get(topic)
        .getListener()
        .clearEvents();
  }

  private void waitForMessage(long timeoutMillis, String expectedValue, TestKafkaListener listener,
      AtomicReference<KafkaMessage> actualKafkaMessageRef, JsonAssertions assertions) {
    try {
      await()
          .atMost(timeoutMillis, MILLISECONDS)
          .pollInterval(100, MILLISECONDS)
          .until(() -> {
            KafkaMessage last = listener.getKafkaMessage(0);
            if (last != null) {
              actualKafkaMessageRef.set(last);
            }
            return last != null && assertions.jsonEquals(expectedValue, last.getValue());
          });
    } catch (ConditionTimeoutException e) {
      if (actualKafkaMessageRef.get() == null) {
        throw new AssertionError("Message not received");
      }
    }
  }

}
