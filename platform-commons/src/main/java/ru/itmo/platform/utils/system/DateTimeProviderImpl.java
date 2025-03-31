package ru.itmo.platform.utils.system;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class DateTimeProviderImpl implements DateTimeProvider {

  @Override
  public Instant now() {
    return Instant.now();
  }

}